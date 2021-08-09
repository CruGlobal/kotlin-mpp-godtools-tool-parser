grammar StateExpression;
@header {
package org.cru.godtools.expressions.grammar.generated;
}

booleanExpr
 : OPAR expr=booleanExpr CPAR             #parExpr
 | NOT expr=booleanExpr                   #notExpr
 | left=booleanExpr AND right=booleanExpr #andExpr
 | left=booleanExpr OR right=booleanExpr  #orExpr
 | FUNC_ISSET varName=VAR CPAR            #isSetFunc
 | varName=VAR op=(EQ | NEQ) value=STRING #eqExpr
 | atom=(TRUE | FALSE)                    #booleanAtom
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

FUNC_ISSET: 'isSet' OPAR;

//INT : [0-9]+;
STRING : ('"' ~["]* '"' | '\'' ~[']* '\'');
VAR : [a-zA-Z] [a-zA-Z_0-9]*;
