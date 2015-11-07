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
    private void printToken(String token){ System.out.println((yyline+1)+": " + token);}
    
%}

/***********************/
/* MACRO DECALARATIONS */
/***********************/
LineTerminator		= \r|\n|\r\n
WhiteSpace			= {LineTerminator} | [ \t\f]
INTEGER				= 0 | [1-9][0-9]*
IDENTIFIER			= [a-z_][A-Za-z_0-9]*
CLASS_IDENTIFIER	= [A-Z][A-Za-z_0-9]*
QUOTABLE			= \\\"|[^\"]
QUOTE				= \"{QUOTABLE}*\"
LINE_COMMENT		= (\/\/[^\r\n]*)
COMMENT				= (\/\*([^*]|\*[^\/*]|\*\*[^\/*])*\*+\/)|{LINE_COMMENT}
   
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
"if"				{ printToken("IF"); return symbol(sym.IF);}
"else"				{ printToken("ELSE"); return symbol(sym.ELSE);}
"while"				{ printToken("WHILE"); return symbol(sym.WHILE);}
"continue"			{ printToken("CONTINUE"); return symbol(sym.CONTINUE);}
"break"				{ printToken("BREAK"); return symbol(sym.BREAK);}
"class"				{ printToken("CLASS"); return symbol(sym.CLASS);}
"extends"			{ printToken("EXTENDS"); return symbol(sym.EXTENDS);}
"return"			{ printToken("RETURN"); return symbol(sym.RETURN);}
"static"			{ printToken("STATIC"); return symbol(sym.STATIC);}
"length"			{ printToken("LENGTH"); return symbol(sym.LENGTH);}
"new"				{ printToken("NEW"); return symbol(sym.NEW);}
"this"				{ printToken("THIS"); return symbol(sym.THIS);}

"void"				{ printToken("VOID"); return symbol(sym.VOID);}
"int"				{ printToken("INT"); return symbol(sym.INT);}
"string"			{ printToken("STRING");return symbol(sym.STRING);}
"boolean"			{ printToken("BOOLEAN"); return symbol(sym.BOOLEAN);}
"true"				{ printToken("TRUE"); return symbol(sym.TRUE);}
"false"				{ printToken("FALSE"); return symbol(sym.FALSE);}
"null"				{ printToken("NULL"); return symbol(sym.NULL);}

"="					{ printToken("ASSIGN"); return symbol(sym.ASSIGN);}

"=="				{ printToken("EQUAL"); return symbol(sym.EQUAL);}
"!="				{ printToken("NEQUAL"); return symbol(sym.NEQUAL);}				
"!"					{ printToken("LNEG"); return symbol(sym.LNEG);}
"&&"				{ printToken("LAND"); return symbol(sym.LAND);}
"||"				{ printToken("LOR"); return symbol(sym.LOR);}

">"					{ printToken("GT"); return symbol(sym.GT);}
">="				{ printToken("GTE"); return symbol(sym.GTE);}
"<"					{ printToken("LT"); return symbol(sym.LT);}
"<="				{ printToken("LTE"); return symbol(sym.LTE);}

"."					{ printToken("DOT"); return symbol(sym.DOT);}
","					{ printToken("COMMA"); return symbol(sym.COMMA);}
";"					{ printToken("SEMI"); return symbol(sym.SEMI);}

"-"					{ printToken("MINUS");      return symbol(sym.MINUS);}
"+"					{ printToken("PLUS");      return symbol(sym.PLUS);}
"*"					{ printToken("MULTIPLY");     return symbol(sym.MULTIPLY);}

"/"					{ printToken("DIVIDE");    return symbol(sym.DIVIDE);}
"%"					{ printToken("MOD");    return symbol(sym.MOD);}

"("					{ printToken("LP");    return symbol(sym.LP);}
")"					{ printToken("RP");    return symbol(sym.RP);}
"["					{ printToken("LB");    return symbol(sym.LB);}
"]"					{ printToken("RB");    return symbol(sym.RB);}
"{"					{ printToken("LCBR");    return symbol(sym.LCBR);}
"}"					{ printToken("RCBR");   return symbol(sym.RCBR);}


{QUOTE}				{
						printToken("QUOTE("+yytext()+")");
						/*
						System.out.print((yyline+1)+ ": QUOTE(");
						System.out.print(yytext());
						System.out.print(")\n");
						*/
						return symbol(sym.QUOTE, new String(yytext()));
					}		
					
{COMMENT}		
					{
						
					}			
{INTEGER}			{
						printToken("INTEGER("+yytext()+")");
						/*
						System.out.print((yyline+1)+ ": INTEGER(");
						System.out.print(yytext());
						System.out.print(")\n");
						*/
						return symbol(sym.INTEGER, new Integer(yytext()));
					}   
{IDENTIFIER}		{
						printToken("ID("+yytext()+")");
						/*
						System.out.print((yyline+1)+ ": ID(");
						System.out.print(yytext());
						System.out.print(")\n");
						*/
						return symbol(sym.ID, new String(yytext()));
					}
{CLASS_IDENTIFIER}	{
						printToken("CLASS_ID("+yytext()+")");
						/*
						System.out.print((yyline+1)+ ": CLASS_ID(");
						System.out.print(yytext());
						System.out.print(")\n");
						*/
						return symbol(sym.CLASS_ID, new String(yytext())); 
					}
{WhiteSpace}		{ /* just skip what was found, do nothing */ }   
}