/**
 * AMDL Lexer (Tokenizer)
 * Converts AMDL source code into tokens
 */

export enum TokenType {
	// Keywords
	AUTOMATON = 'AUTOMATON',
	STATE = 'STATE',
	ALPHABET = 'ALPHABET',
	TRUE = 'TRUE',
	FALSE = 'FALSE',
	EPSILON = 'EPSILON',

	// Symbols
	LBRACE = 'LBRACE', // {
	RBRACE = 'RBRACE', // }
	LPAREN = 'LPAREN', // (
	RPAREN = 'RPAREN', // )
	COLON = 'COLON', // :
	COMMA = 'COMMA', // ,
	ARROW = 'ARROW', // ->
	DASH = 'DASH', // -

	// Literals
	IDENTIFIER = 'IDENTIFIER',
	STRING = 'STRING',
	NUMBER = 'NUMBER',

	// Special
	COMMENT = 'COMMENT',
	EOF = 'EOF'
}

export interface Token {
	type: TokenType;
	value: string;
	line: number;
	column: number;
}

export class Lexer {
	private input: string;
	private position = 0;
	private line = 1;
	private column = 1;

	constructor(input: string) {
		this.input = input;
	}

	tokenize(): Token[] {
		const tokens: Token[] = [];

		while (this.position < this.input.length) {
			this.skipWhitespace();

			if (this.position >= this.input.length) break;

			// Skip comments
			if (this.peek() === '/' && this.peekNext() === '/') {
				this.skipLineComment();
				continue;
			}

			if (this.peek() === '/' && this.peekNext() === '*') {
				this.skipBlockComment();
				continue;
			}

			const token = this.nextToken();
			if (token.type !== TokenType.COMMENT) {
				tokens.push(token);
			}
		}

		tokens.push({
			type: TokenType.EOF,
			value: '',
			line: this.line,
			column: this.column
		});

		return tokens;
	}

	private nextToken(): Token {
		const char = this.peek();
		const line = this.line;
		const column = this.column;

		// Single character tokens
		if (char === '{') {
			this.advance();
			return { type: TokenType.LBRACE, value: '{', line, column };
		}
		if (char === '}') {
			this.advance();
			return { type: TokenType.RBRACE, value: '}', line, column };
		}
		if (char === '(') {
			this.advance();
			return { type: TokenType.LPAREN, value: '(', line, column };
		}
		if (char === ')') {
			this.advance();
			return { type: TokenType.RPAREN, value: ')', line, column };
		}
		if (char === ':') {
			this.advance();
			return { type: TokenType.COLON, value: ':', line, column };
		}
		if (char === ',') {
			this.advance();
			return { type: TokenType.COMMA, value: ',', line, column };
		}

		// Arrow ->
		if (char === '-') {
			this.advance();
			if (this.peek() === '>') {
				this.advance();
				return { type: TokenType.ARROW, value: '->', line, column };
			}
			return { type: TokenType.DASH, value: '-', line, column };
		}

		// String
		if (char === '"') {
			return this.readString(line, column);
		}

		// Number
		if (this.isDigit(char)) {
			return this.readNumber(line, column);
		}

		// Identifier or Keyword
		if (this.isAlpha(char) || char === '_' || char === 'ε') {
			return this.readIdentifierOrKeyword(line, column);
		}

		throw new Error(`Unexpected character '${char}' at line ${line}, column ${column}`);
	}

	private readString(line: number, column: number): Token {
		this.advance(); // Skip opening "
		let value = '';

		while (this.peek() !== '"' && this.position < this.input.length) {
			if (this.peek() === '\\') {
				this.advance();
				const escaped = this.peek();
				if (escaped === 'n') value += '\n';
				else if (escaped === 't') value += '\t';
				else if (escaped === '"') value += '"';
				else if (escaped === '\\') value += '\\';
				else value += escaped;
				this.advance();
			} else {
				value += this.peek();
				this.advance();
			}
		}

		if (this.peek() !== '"') {
			throw new Error(`Unterminated string at line ${line}, column ${column}`);
		}

		this.advance(); // Skip closing "

		return { type: TokenType.STRING, value, line, column };
	}

	private readNumber(line: number, column: number): Token {
		let value = '';

		while (this.isDigit(this.peek()) || this.peek() === '.') {
			value += this.peek();
			this.advance();
		}

		return { type: TokenType.NUMBER, value, line, column };
	}

	private readIdentifierOrKeyword(line: number, column: number): Token {
		let value = '';

		while (
			this.isAlphaNumeric(this.peek()) ||
			this.peek() === '_' ||
			this.peek() === 'ε'
		) {
			value += this.peek();
			this.advance();
		}

		// Check for keywords
		const type = this.getKeywordType(value);

		return { type, value, line, column };
	}

	private getKeywordType(value: string): TokenType {
		switch (value.toLowerCase()) {
			case 'automaton':
				return TokenType.AUTOMATON;
			case 'state':
				return TokenType.STATE;
			case 'alphabet':
				return TokenType.ALPHABET;
			case 'true':
				return TokenType.TRUE;
			case 'false':
				return TokenType.FALSE;
			case 'epsilon':
			case 'ε':
				return TokenType.EPSILON;
			default:
				return TokenType.IDENTIFIER;
		}
	}

	private skipWhitespace(): void {
		while (this.position < this.input.length && this.isWhitespace(this.peek())) {
			if (this.peek() === '\n') {
				this.line++;
				this.column = 1;
			} else {
				this.column++;
			}
			this.position++;
		}
	}

	private skipLineComment(): void {
		// Skip //
		this.advance();
		this.advance();

		while (this.peek() !== '\n' && this.position < this.input.length) {
			this.advance();
		}
	}

	private skipBlockComment(): void {
		// Skip /*
		this.advance();
		this.advance();

		while (this.position < this.input.length) {
			if (this.peek() === '*' && this.peekNext() === '/') {
				this.advance(); // Skip *
				this.advance(); // Skip /
				break;
			}
			this.advance();
		}
	}

	private peek(): string {
		return this.input[this.position] || '';
	}

	private peekNext(): string {
		return this.input[this.position + 1] || '';
	}

	private advance(): void {
		if (this.peek() === '\n') {
			this.line++;
			this.column = 1;
		} else {
			this.column++;
		}
		this.position++;
	}

	private isWhitespace(char: string): boolean {
		return /\s/.test(char);
	}

	private isDigit(char: string): boolean {
		return /[0-9]/.test(char);
	}

	private isAlpha(char: string): boolean {
		return /[a-zA-Z]/.test(char);
	}

	private isAlphaNumeric(char: string): boolean {
		return this.isAlpha(char) || this.isDigit(char);
	}
}
