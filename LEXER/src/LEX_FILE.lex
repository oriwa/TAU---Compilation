/***************************/
/* FILE NAME: LEX_FILE.lex */
/***************************/

/***************************/
/* AUTHOR: OREN ISH SHALOM */
/***************************/

/*************/
/* USER CODE */
/*************/
   
import java_cup.runtime.*;



/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/
      
%%
   
/************************************/
/* OPTIONS AND DECLARATIONS SECTION */
/************************************/
   
/*****************************************************/ 
/* Lexer is the name of the class JFlex will create. */
/* The code will be written to the file Lexer.java.  */
/*****************************************************/ 
%class Lexer

/********************************************************************/
/* The current line number can be accessed with the variable yyline */
/* and the current column number with the variable yycolumn.        */
/********************************************************************/
%line
%column
    
/******************************************************************/
/* CUP compatibility mode interfaces with a CUP generated parser. */
/******************************************************************/
%cup
   
/****************/
/* DECLARATIONS */
/****************/
%state COMMENTS

/*****************************************************************************/   
/* Code between %{ and %}, both of which must be at the beginning of a line, */
/* will be copied letter to letter into the Lexer class code.                */
/* Here you declare member variables and functions that are used inside the  */
/* scanner actions.                                                          */  
/*****************************************************************************/   
%{   
    /*********************************************************************************/
    /* Create a new java_cup.runtime.Symbol with information about the current token */
    /*********************************************************************************/
    private Symbol symbol(int type)               {return new Symbol(type, yyline, yycolumn);}
    private Symbol symbol(int type, Object value) {return new Symbol(type, yyline, yycolumn, value);}
    private void printWithLineNumber(String token){ System.out.println((yyline+1)+": " + token);}
    private Symbol printErrorAndExit(String error){ 
    	printWithLineNumber("Lexical error: " + error);
    	return symbol(sym.EOF);
    }
    
%}

/***********************/
/* MACRO DECALARATIONS */
/***********************/
LineTerminator		= \r|\n|\r\n
WhiteSpace			= {LineTerminator} | [ \t\f]
INTEGER				= 0|[1-9][0-9]*
IDENTIFIER			= [a-z_][A-Za-z_0-9]*
CLASS_IDENTIFIER	= [A-Z][A-Za-z_0-9]*
QUOTABLE_ASCII		= ([\x20-\x21, \x23-\x5B, \x5D-\x7E]) /* printable ASCII characters other than quote " and backslash \ */
QUOTABLE_ESCAPE		= (\\\"|\\\\|\\t|\\n) /* the escape sequences \", \\, \t, and \n */
QUOTABLE_CHARACTER 	= ({QUOTABLE_ASCII}|{QUOTABLE_ESCAPE})
QUOTE_UNCLOSED		= (\"({QUOTABLE_CHARACTER})*)
QUOTE				= {QUOTE_UNCLOSED}\"
COMMENT_LINE		= \/\/.*
/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/

%%

/************************************************************/
/* LEXER matches regular expressions to actions (Java code) */
/************************************************************/
   
/**************************************************************/
/* YYINITIAL is the state at which the lexer begins scanning. */
/* So these regular expressions will only be matched if the   */
/* scanner is in the start state YYINITIAL.                   */
/**************************************************************/


<YYINITIAL> {
"if"				{ printWithLineNumber("IF"); return symbol(sym.IF);}
"else"				{ printWithLineNumber("ELSE"); return symbol(sym.ELSE);}
"while"				{ printWithLineNumber("WHILE"); return symbol(sym.WHILE);}
"continue"			{ printWithLineNumber("CONTINUE"); return symbol(sym.CONTINUE);}
"break"				{ printWithLineNumber("BREAK"); return symbol(sym.BREAK);}
"class"				{ printWithLineNumber("CLASS"); return symbol(sym.CLASS);}
"extends"			{ printWithLineNumber("EXTENDS"); return symbol(sym.EXTENDS);}
"return"			{ printWithLineNumber("RETURN"); return symbol(sym.RETURN);}
"static"			{ printWithLineNumber("STATIC"); return symbol(sym.STATIC);}
"length"			{ printWithLineNumber("LENGTH"); return symbol(sym.LENGTH);}
"new"				{ printWithLineNumber("NEW"); return symbol(sym.NEW);}
"this"				{ printWithLineNumber("THIS"); return symbol(sym.THIS);}

"void"				{ printWithLineNumber("VOID"); return symbol(sym.VOID);}
"int"				{ printWithLineNumber("INT"); return symbol(sym.INT);}
"string"			{ printWithLineNumber("STRING");return symbol(sym.STRING);}
"boolean"			{ printWithLineNumber("BOOLEAN"); return symbol(sym.BOOLEAN);}
"true"				{ printWithLineNumber("TRUE"); return symbol(sym.TRUE);}
"false"				{ printWithLineNumber("FALSE"); return symbol(sym.FALSE);}
"null"				{ printWithLineNumber("NULL"); return symbol(sym.NULL);}

"="					{ printWithLineNumber("ASSIGN"); return symbol(sym.ASSIGN);}

"=="				{ printWithLineNumber("EQUAL"); return symbol(sym.EQUAL);}
"!="				{ printWithLineNumber("NEQUAL"); return symbol(sym.NEQUAL);}				
"!"					{ printWithLineNumber("LNEG"); return symbol(sym.LNEG);}
"&&"				{ printWithLineNumber("LAND"); return symbol(sym.LAND);}
"||"				{ printWithLineNumber("LOR"); return symbol(sym.LOR);}

">"					{ printWithLineNumber("GT"); return symbol(sym.GT);}
">="				{ printWithLineNumber("GTE"); return symbol(sym.GTE);}
"<"					{ printWithLineNumber("LT"); return symbol(sym.LT);}
"<="				{ printWithLineNumber("LTE"); return symbol(sym.LTE);}

"."					{ printWithLineNumber("DOT"); return symbol(sym.DOT);}
","					{ printWithLineNumber("COMMA"); return symbol(sym.COMMA);}
";"					{ printWithLineNumber("SEMI"); return symbol(sym.SEMI);}

"-"					{ printWithLineNumber("MINUS");      return symbol(sym.MINUS);}
"+"					{ printWithLineNumber("PLUS");      return symbol(sym.PLUS);}
"*"					{ printWithLineNumber("MULTIPLY");     return symbol(sym.MULTIPLY);}

"/"					{ printWithLineNumber("DIVIDE");    return symbol(sym.DIVIDE);}
"%"					{ printWithLineNumber("MOD");    return symbol(sym.MOD);}

"("					{ printWithLineNumber("LP");    return symbol(sym.LP);}
")"					{ printWithLineNumber("RP");    return symbol(sym.RP);}
"["					{ printWithLineNumber("LB");    return symbol(sym.LB);}
"]"					{ printWithLineNumber("RB");    return symbol(sym.RB);}
"{"					{ printWithLineNumber("LCBR");    return symbol(sym.LCBR);}
"}"					{ printWithLineNumber("RCBR");   return symbol(sym.RCBR);}

"/*"              	{ yybegin(COMMENTS);}

<<EOF>>				{ printWithLineNumber("EOF");	return  symbol(sym.EOF);}

{QUOTE}				{
						printWithLineNumber("QUOTE("+yytext()+")");
						return symbol(sym.QUOTE, new String(yytext()));
					}
										
{QUOTE_UNCLOSED}	{	return printErrorAndExit("String missing closing quote symbol"); }	
						
{INTEGER}			{
						printWithLineNumber("INTEGER("+yytext()+")");
						return symbol(sym.INTEGER, new Integer(yytext()));
					}   
					
{IDENTIFIER}		{
						printWithLineNumber("ID("+yytext()+")");
						return symbol(sym.ID, new String(yytext()));
					}
			
{CLASS_IDENTIFIER}	{
						printWithLineNumber("CLASS_ID("+yytext()+")");
						return symbol(sym.CLASS_ID, new String(yytext())); 
					}

{COMMENT_LINE} 		{ /* skip comment */ }

{WhiteSpace}		{ /* just skip what was found, do nothing */ }

.					{ return printErrorAndExit("Illegal character '" + yytext() +"'");}
   
}

<COMMENTS> {
	 "*/"      	{ yybegin(YYINITIAL);}
     .|{LineTerminator}		   {/* everything is alowed inside comments */}
     <<EOF>>	{ return printErrorAndExit("Unclosed comment");}
}