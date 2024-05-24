grammar JSON;

value: STRING | NUMBER | object | array | BOOLEAN | NULLVALUE | variable;

object: '{' (pair (',' pair)*)? '}';

pair: STRING ':' value;

array: '[' (value (',' value)*)? ']';

script: instruction+ ;

instruction: loadStatement | saveStatement | assign;

loadStatement: 'load' PARAMETRO 'to' ID;

saveStatement: 'save' ID 'to' PARAMETRO;

assign: ID '=' expression;

expression: expressionAccess | value | variable;

expressionAccess : ID('.' ID)*('|' OP)?;

variable: ID;

OP: 'SUM' | 'MAX' | 'MIN' | 'COUNT' | 'AVG';

PARAMETRO:'$'INT;

ID: [a-zA-Z_][a-zA-Z0-9_]*;

STRING: '"' (ESC | ~["\\])* '"';

NUMBER: '-'? INT '.' INT | '-'? INT | '-'? '.' INT;

INT: '0' | [1-9] [0-9]*;

BOOLEAN: 'true' | 'false';

NULLVALUE: 'null';

WHITESPACE : [ \t\r\n]+ -> skip ;

fragment ESC: '\\' (["\\/bfnrt] | UNICODE);

fragment UNICODE: 'u' HEX HEX HEX HEX;

fragment HEX: [0-9a-fA-F];
