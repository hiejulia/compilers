yylex(void)                 /* miniC */
{
while((c = getc(fin)) == ‘ ‘ || c == ‘\t’)
       ;
if(c == EOF)
       return 0;
if(c == ‘\\’) {
       c = getc(fin);
       if(c == ‘\n’) {
               lineno++;
               return yylex();
       }
}
if(c == ‘#’) {             /* comment */
        while((c = getc(fin)) != ‘\n’ && c != EOF)
                ;
        if(c == ‘\n’)
                lineno++;
        return c;
}
if(c == ‘.’ || isdigit(c)) {             /* number */
        double d;
        char numb[20], numb2[20];
        int myi;
        Symbol *s;
        ungetc(c, fin);
        fscanf(fin, ″%[0–9.eE+–]″, numb2);
        if(   strchr(numb2,’.’) != NULL
           || strchr(numb2,’e’) != NULL
           || strchr(numb2,’E’) != NULL){
           sscanf(numb2, ″%lf″, &d);
           sprintf(numb,″%11.7e″,(float)d);
           if((s = lookup(numb)) == 0)
               s = install(numb, NUMBER, d);
           yylval.sym = s;
           return NUMBER;
        }else {                                                              /* integer */
           sscanf(numb2, ″%d″, &myi);
           sprintf(numb,″%d″ , myi);
           if((s = lookup(numb)) == 0)
               s = install(numb, INT, myi);
           yylval.sym = s;
           return INT;
        }
}
if(isalpha(c) || c == ‘_’){
        Symbol *s;
        char sbuf[100], *p = sbuf;
        do {
               if(p <= sbuf + sizeof(sbuf) – 1) {
                               *p = ‘\0’;
                               execerror(″name too long″, sbuf);
                }
                *p++ = c;
        } while((c = getc(fin)) != EOF && (isalnum(c) || c == ‘_’));
        ungetc(c, fin);
        *p = ‘\0’;
        if((s = lookup(sbuf)) == 0)
                s = install(sbuf, UNDEF, 0.0);
        yylval.sym = s;
        return type(s) == UNDEF ? VAR : type(s);
}
if(c == ‘$’) { /* argument? */
        int n = 0;
        while(isdigit(c = getc(fin)))
                n = 10 * n + c – ‘0’;
        ungetc(c, fin);
        if(n == 0)
                execerror(″strange $…″, (char *)0);
        yylval.narg = n;
        return ARG;
}
if(c == ‘″’) { /* quoted string */
        char sbuf[100], *p;
        for(p = sbuf; (c = getc(fin)) != ‘″’ ; p++) {
                if(c == ‘\n’ || c == EOF)
                        execerror(″missing quote″, ″″);
                if(p <= sbuf + sizeof(sbuf) – 1) { *p = ‘\0’;
                        execerror(″string too long″, sbuf);
                }
                *p = backslash(c);
       }
       *p = 0;
       yylval.sym = (Symbol *)emalloc(strlen(sbuf) + 1);
       strcpy((char*)yylval.sym, sbuf);
       return STRING;
}
switch(c) {
case ‘+’:     return follow(’+’, INC, follow(’=’, ADDEQ, ‘+’));
case ‘–’:     return follow(’–’, DEC, follow(’=’, SUBEQ, ‘–’));
case ‘*’:     return follow(’=’, MULEQ, ‘*’);
case ‘/’:     return follow(’=’, DIVEQ, ‘/’);
case ‘%’:     return follow(’=’, MODEQ, ‘%’);
case ‘>’:     return follow(’=’, GE, GT);
case ‘<’:     return follow(’=’, LE, LT);
case ‘=’:     return follow(’=’, EQ, ‘=’);
case ‘!’:     return follow(’=’, NE, NOT);
case ‘|’:     return follow(’|’, OR, ‘|’);
case ‘&’:     return follow(’&’, AND, ‘&’);
case ‘\n’:    lineno++; return ‘\n’;
default:      return c;
        }
}
backslash(int c)     /* get next char with \'s interpreted */
{
        static char transtab[] = ″b\bf\fn\nr\rt\t″;
        if(c != ‘\\’)
                return c;
        c = getc(fin);
        if(islower(c) && strchr(transtab, c))
                return strchr(transtab, c)[1];
        return c;
}

follow(int expect, int ifyes, int ifno) /* look ahead for >=, etc. */
{
        int c = getc(fin);

        if(c == expect)
                return ifyes;
        ungetc(c, fin);
        return ifno;
}