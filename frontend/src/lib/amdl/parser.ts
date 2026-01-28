/**
 * AMDL Parser
 * Parses tokens into an Abstract Syntax Tree (AST)
 */

import { Token, TokenType, Lexer } from './lexer';

// AST Node types
export interface AutomatonNode {
	type: 'Automaton';
	name: string;
	metadata: Record<string, string>;
	alphabet: string[];
	states: StateNode[];
	transitions: TransitionNode[];
}

export interface StateNode {
	type: 'State';
	id: string;
	initial: boolean;
	accepting: boolean;
	position?: { x: number; y: number };
	label?: string;
}

export interface TransitionNode {
	type: 'Transition';
	from: string;
	to: string;
	symbols: string[];
}

export class Parser {
	private tokens: Token[];
	private current = 0;

	constructor(input: string) {
		const lexer = new Lexer(input);
		this.tokens = lexer.tokenize();
	}

	parse(): AutomatonNode {
		return this.parseAutomaton();
	}

	private parseAutomaton(): AutomatonNode {
		this.expect(TokenType.AUTOMATON);

		const name = this.expect(TokenType.STRING).value;

		this.expect(TokenType.LBRACE);

		const metadata: Record<string, string> = {};
		const alphabet: string[] = [];
		const states: StateNode[] = [];
		const transitions: TransitionNode[] = [];

		while (!this.check(TokenType.RBRACE)) {
			// Try to parse metadata
			if (this.checkIdentifier() && this.peekNext()?.type === TokenType.COLON) {
				const { key, value } = this.parseMetadata();
				metadata[key] = value;
				continue;
			}

			// Try to parse alphabet
			if (this.check(TokenType.ALPHABET)) {
				const symbols = this.parseAlphabet();
				alphabet.push(...symbols);
				continue;
			}

			// Try to parse state
			if (this.check(TokenType.STATE)) {
				const state = this.parseState();
				states.push(state);
				continue;
			}

			// Try to parse transition
			if (this.checkIdentifier()) {
				const transition = this.parseTransition();
				transitions.push(transition);
				continue;
			}

			throw this.error('Unexpected token');
		}

		this.expect(TokenType.RBRACE);

		return {
			type: 'Automaton',
			name,
			metadata,
			alphabet,
			states,
			transitions
		};
	}

	private parseMetadata(): { key: string; value: string } {
		const key = this.expect(TokenType.IDENTIFIER).value;
		this.expect(TokenType.COLON);

		let value: string;
		if (this.check(TokenType.STRING)) {
			value = this.advance().value;
		} else if (this.check(TokenType.NUMBER)) {
			value = this.advance().value;
		} else if (this.check(TokenType.IDENTIFIER)) {
			value = this.advance().value;
		} else {
			throw this.error('Expected string, number, or identifier for metadata value');
		}

		return { key, value };
	}

	private parseAlphabet(): string[] {
		this.expect(TokenType.ALPHABET);
		this.expect(TokenType.COLON);

		const symbols: string[] = [];

		do {
			if (this.check(TokenType.COMMA)) {
				this.advance();
			}

			const symbol = this.parseSymbol();
			symbols.push(symbol);
		} while (this.check(TokenType.COMMA));

		return symbols;
	}

	private parseState(): StateNode {
		this.expect(TokenType.STATE);

		const id = this.expect(TokenType.IDENTIFIER).value;

		this.expect(TokenType.LBRACE);

		let initial = false;
		let accepting = false;
		let position: { x: number; y: number } | undefined;
		let label: string | undefined;

		while (!this.check(TokenType.RBRACE)) {
			const prop = this.expect(TokenType.IDENTIFIER).value;
			this.expect(TokenType.COLON);

			switch (prop) {
				case 'initial':
					initial = this.parseBoolean();
					break;
				case 'accepting':
					accepting = this.parseBoolean();
					break;
				case 'position':
					position = this.parsePosition();
					break;
				case 'label':
					label = this.expect(TokenType.STRING).value;
					break;
				default:
					throw this.error(`Unknown state property: ${prop}`);
			}
		}

		this.expect(TokenType.RBRACE);

		return {
			type: 'State',
			id,
			initial,
			accepting,
			position,
			label
		};
	}

	private parseTransition(): TransitionNode {
		const from = this.expect(TokenType.IDENTIFIER).value;

		this.expect(TokenType.DASH);

		const symbols: string[] = [];

		do {
			if (this.check(TokenType.COMMA)) {
				this.advance();
			}

			const symbol = this.parseSymbol();
			symbols.push(symbol);
		} while (this.check(TokenType.COMMA));

		this.expect(TokenType.ARROW);

		const to = this.expect(TokenType.IDENTIFIER).value;

		return {
			type: 'Transition',
			from,
			to,
			symbols
		};
	}

	private parseSymbol(): string {
		if (this.check(TokenType.IDENTIFIER)) {
			return this.advance().value;
		} else if (this.check(TokenType.EPSILON)) {
			this.advance();
			return 'ε';
		} else if (this.check(TokenType.NUMBER)) {
			return this.advance().value;
		} else {
			throw this.error('Expected symbol (identifier, epsilon, or number)');
		}
	}

	private parseBoolean(): boolean {
		if (this.check(TokenType.TRUE)) {
			this.advance();
			return true;
		} else if (this.check(TokenType.FALSE)) {
			this.advance();
			return false;
		} else {
			throw this.error('Expected boolean (true or false)');
		}
	}

	private parsePosition(): { x: number; y: number } {
		this.expect(TokenType.LPAREN);
		const x = parseFloat(this.expect(TokenType.NUMBER).value);
		this.expect(TokenType.COMMA);
		const y = parseFloat(this.expect(TokenType.NUMBER).value);
		this.expect(TokenType.RPAREN);

		return { x, y };
	}

	// Helper methods

	private check(type: TokenType): boolean {
		return this.peek().type === type;
	}

	private checkIdentifier(): boolean {
		return this.peek().type === TokenType.IDENTIFIER;
	}

	private peek(): Token {
		return this.tokens[this.current];
	}

	private peekNext(): Token | undefined {
		return this.tokens[this.current + 1];
	}

	private advance(): Token {
		const token = this.tokens[this.current];
		this.current++;
		return token;
	}

	private expect(type: TokenType): Token {
		const token = this.peek();
		if (token.type !== type) {
			throw this.error(`Expected ${type}, got ${token.type}`);
		}
		return this.advance();
	}

	private error(message: string): Error {
		const token = this.peek();
		return new Error(`${message} at line ${token.line}, column ${token.column}`);
	}
}
