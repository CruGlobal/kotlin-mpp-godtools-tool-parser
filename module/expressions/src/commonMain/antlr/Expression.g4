grammar Expression;

//eval : expr;
//
expr
 : OPAR expr CPAR           #parExpr
 | NOT expr                 #notExpr
 | (TRUE | FALSE)           #booleanAtom
 | VAR op=(EQ | NEQ) STRING #eqExpr
// | expr AND expr           #andExpr
// | expr OR expr            #orExpr
 ;

//atom
// : VAR    #varAtom
// | STRING #stringAtom
// | INT            #numberAtom
// | NIL            #nilAtom
// ;
//
EQ : '==';
NEQ : '!=';
AND : '&&';
OR : '||';
NOT : '!';

OPAR : '(';
CPAR : ')';
TRUE : 'true';
FALSE : 'false';
//NIL : 'nil';

//INT : [0-9]+;
STRING : ('"' ~["]* '"' | '\'' ~[']* '\'');
VAR : [a-zA-Z] [a-zA-Z_0-9]*;
