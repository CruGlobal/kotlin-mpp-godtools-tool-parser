grammar StateExpression;

booleanExpr
 : OPAR expr=booleanExpr CPAR                                     #parExpr
 | NOT expr=booleanExpr                                           #notExpr
 | left=booleanExpr AND right=booleanExpr                         #andExpr
 | left=booleanExpr OR right=booleanExpr                          #orExpr
 | FUNC_ISSET varName=VAR CPAR                                    #isSetFunc
 | varName=VAR op=(EQ | NEQ) value=STRING                         #eqExpr
 | left=intExpr op=(EQ | NEQ | GTE | GT | LTE | LT) right=intExpr #intCmpExpr
 | atom=(TRUE | FALSE)                                            #booleanAtom
 ;

intExpr
 : OPAR expr=intExpr CPAR       #parIntExpr
 | FUNC_VALUES varName=VAR CPAR #valuesFunc
 | value=INT                    #intAtom
 ;

//atom
// : VAR    #varAtom
// | STRING #stringAtom
// | INT            #numberAtom
// | NIL            #nilAtom
// ;
//
EQ  : '==';
NEQ : '!=';
AND : '&&';
OR  : '||';
NOT : '!';
GTE : '>=';
GT  : '>';
LTE : '<=';
LT  : '<';

OPAR : '(';
CPAR : ')';

TRUE  : 'true';
FALSE : 'false';
//NIL : 'nil';

FUNC_ISSET  : 'isSet' OPAR;
FUNC_VALUES : 'values' OPAR;

INT    : [0-9]+;
STRING : ('"' ~["]* '"' | '\'' ~[']* '\'');
VAR    : [a-zA-Z] [a-zA-Z_0-9]*;
