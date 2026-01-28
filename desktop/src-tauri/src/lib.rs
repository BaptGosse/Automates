#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

use std::process::{Command, Child};
use std::net::TcpListener;
use std::sync::{Arc, Mutex};
use tauri::{Manager, State};

struct BackendProcess {
    child: Arc<Mutex<Option<Child>>>,
    port: u16,
}

/// Trouve un port TCP libre
fn find_free_port() -> u16 {
    TcpListener::bind("127.0.0.1:0")
        .unwrap()
        .local_addr()
        .unwrap()
        .port()
}

/// Démarre le backend Spring Boot
fn start_backend(port: u16, resource_dir: &std::path::Path) -> std::io::Result<Child> {
    let jar_path = resource_dir.join("backend.jar");

    println!("Démarrage du backend Java sur le port {}", port);
    println!("JAR path: {:?}", jar_path);

    Command::new("java")
        .arg("-jar")
        .arg(&jar_path)
        .arg(format!("--server.port={}", port))
        .arg("--spring.profiles.active=desktop")
        .arg("--logging.level.root=INFO")
        .spawn()
}

/// Commande Tauri pour obtenir l'URL de l'API
#[tauri::command]
fn get_api_url(state: State<BackendProcess>) -> String {
    format!("http://localhost:{}/api", state.port)
}

/// Commande Tauri pour vérifier si le backend est prêt
#[tauri::command]
fn is_backend_ready(state: State<'_, BackendProcess>) -> Result<bool, String> {
    let url = format!("http://localhost:{}/actuator/health", state.port);
    println!("Health check sur: {}", url);

    match reqwest::blocking::get(&url) {
        Ok(response) => {
            let status = response.status();
            println!("Health check réponse: {}", status);
            Ok(status.is_success())
        },
        Err(e) => {
            println!("Health check erreur: {}", e);
            Ok(false)
        }
    }
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    let port = find_free_port();

    tauri::Builder::default()
        .plugin(tauri_plugin_opener::init())
        .setup(move |app| {
            let resource_path = app.path()
                .resource_dir()
                .expect("failed to resolve resource dir");

            match start_backend(port, &resource_path) {
                Ok(child) => {
                    println!("Backend démarré avec succès");
                    app.manage(BackendProcess {
                        child: Arc::new(Mutex::new(Some(child))),
                        port,
                    });
                },
                Err(e) => {
                    eprintln!("Erreur lors du démarrage du backend: {}", e);
                    return Err(Box::new(e) as Box<dyn std::error::Error>);
                }
            }

            Ok(())
        })
        .invoke_handler(tauri::generate_handler![get_api_url, is_backend_ready])
        .build(tauri::generate_context!())
        .expect("error while building tauri application")
        .run(|app_handle, event| {
            if let tauri::RunEvent::ExitRequested { .. } = event {
                // Arrêter le backend proprement
                println!("Fermeture de l'application, arrêt du backend...");
                if let Some(state) = app_handle.try_state::<BackendProcess>() {
                    if let Ok(mut child_lock) = state.child.lock() {
                        if let Some(mut child) = child_lock.take() {
                            let _ = child.kill();
                            println!("Backend arrêté");
                        }
                    }
                }
            }
        });
}
