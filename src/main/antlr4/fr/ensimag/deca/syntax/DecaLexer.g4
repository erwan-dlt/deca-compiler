
lexer grammar DecaLexer;

options {
   language = Java;
   // Tell ANTLR to make the generated lexer class extend the
   // the named class, which is where any supporting code and
   // variables will be placed.
   superClass = AbstractDecaLexer;
}

@members {
}

// Deca lexer rules.

// Caractères de formatage - Séparateurs
EOL : '\n' { skip(); }; // End Of Line
SEP : (' ' | '\t' | '\r' | EOL )+ { skip(); };

// Commentaires
CLASSIC_COM     : '/*' .*? '*/' { skip(); };
SINGLE_LINE_COM : '//' ~[\r\n]* { skip(); };

// Mots réservés
ASM        : 'asm';
CLASS      : 'class';
EXTENDS    : 'extends';
ELSE       : 'else';
FALSE      : 'false';
IF         : 'if';
INSTANCEOF : 'instanceof';
NEW        : 'new';
NULL       : 'null';
READINT    : 'readInt';
READFLOAT  : 'readFloat';
PRINT      : 'print';
PRINTLN    : 'println';
PRINTLNX   : 'printlnx';
PRINTX     : 'printx';
PROTECTED  : 'protected';
RETURN     : 'return';
THIS       : 'this';
TRUE       : 'true';
WHILE      : 'while';

// Identificateurs
fragment LETTER : [a-zA-Z];
fragment DIGIT  : '0'..'9';
IDENT : (LETTER | '$' | '_')(LETTER | DIGIT | '$' | '_')*;

// Symboles spéciaux
LT        : '<';
GT        : '>';
EQ        : '=';
PLUS      : '+';
MINUS     : '-';
STAR      : '*';
DIV       : '/';
MOD       : '%';
DOT       : '.';
COMMA     : ',';
LPAREN    : '(';
RPAREN    : ')';
LBRACE    : '{';
RBRACE    : '}';
NOT       : '!';
SEMICOLON : ';';
EQEQ      : '==';
NOTEQ     : '!=';
GTEQ      : '>=';
LTEQ      : '<=';
ANDAND    : '&&';
OROR      : '||';

// Littéraux entiers
fragment POSITIVE_DIGIT : [1-9];
INT : '0' | POSITIVE_DIGIT DIGIT*;

// Littéraux flottants
fragment NUM      : DIGIT+;
fragment DIGITHEX : [0-9A-Fa-f];
fragment NUMHEX   : DIGITHEX+;
fragment SIGN     : '+' | '-';
fragment EXP      : ('E' | 'e') SIGN? NUM;
fragment DEC      : NUM '.' NUM;
fragment FLOATDEC : DEC (EXP)? [Ff]?;
fragment FLOATHEX : ('0x' | '0X') NUMHEX '.' NUMHEX ('P' | 'p') SIGN? NUM [Ff]?;
FLOAT    : FLOATDEC | FLOATHEX;

// Chaînes de caractères
fragment STRING_CAR : ~["\\\n];
STRING
    : '"' (STRING_CAR | '\\"' | '\\\\')* '"' {
        setText(getText().substring(1, getText().length() - 1)); // Retire les guillemets
    };
MULTI_LINE_STRING
    : '"' (STRING_CAR | '\\"' | '\\\\' | EOL)* '"' {
        setText(getText().substring(1, getText().length() - 1)); // Retire les guillemets
    };


// Inclusion de fichier
fragment FILENAME : (LETTER | DIGIT | '.' | '-' | '_')+;
INCLUDE : '#include' (' ')* '"' FILENAME '"' { doInclude(getText()); };

