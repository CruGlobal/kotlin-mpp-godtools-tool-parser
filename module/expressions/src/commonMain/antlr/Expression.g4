grammar Expression;

expr
 : OPAR expr CPAR           #parExpr
 | NOT expr                 #notExpr
 | left=expr AND right=expr #andExpr
 | left=expr OR right=expr  #orExpr
 | VAR op=(EQ | NEQ) STRING #eqExpr
 | atom=(TRUE | FALSE)      #booleanAtom
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
