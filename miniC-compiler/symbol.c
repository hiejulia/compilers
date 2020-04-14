#define L(n)            ((n)–>link[0])
#define R(n)            ((n)–>link[1])
#define name(n)         ((n)–>w.S)
#define type(n)         ((n)–>v.I)       // syntactic type
#define fval(n)         ((n)–>u.F)
#define ival(n)         ((n)–>u.I)
#define ptr(n)          ((n)–>u.P)
#define defn(n)         ((n)–>u.P)
#define str(n)          ((n)–>u.S)
#define ttree(n)        ((n)–>z.N)       // link to type tree contruct
#define subtype(n)      ((n)–>y.I)       // data type
#define nxtinblk(n)     ((n)–>x.N)       // next identifier in the same block


typedef union{
  struct node_struct*N;
  char *S;
  long I;
  float F;
  void *P;
}util;
typedef struct node_struct{
struct node_struct *link[2]; // left = 0, right = 1
util u,v,w,x,y,z;
}Node;
typedef Node Symbol;




Symbol *symtab; /* symbol table */
Symbol* lookup(char* s){        /* find s in symbol table */
        Symbol *sp;
        sp = malloc(sizeof(Symbol));
        name(sp) = malloc(strlen(s) + 1);
        strcpy(name(sp), s);
        sp = search_node(symtab, sp);
        return sp;
}
Symbol* install(char* s, int t, double d){/* install s in symbol table */
        Symbol *sp;
        sp = malloc(sizeof(Symbol));
        name(sp) = malloc(strlen(s) + 1);
        strcpy(name(sp), s);
        type(sp) = t;
        if(t == INT || t == IVAR) ival(sp) = (int)d;
        else if(t == NUMBER || t == VAR) fval(sp) = (float)d;
        insert_node(&symtab, sp);
        return sp;
}
int comp(Node *i, Node *j){
  return strcmp(i–>w.S, j–>w.S);
}
void display_value(Node *n){
  if(n == NULL) printf(″No value available\n″);
  else printf(″[%s](%d){%x}\n″, n–>w.S,n–>v.I,n–>u.P);
}




void insert_node(Node **r, Node *n){
  if((*r) == NULL)
    {
     (*r) = n;
     L(*r) = NULL;
     R(*r) = NULL;
     return;                    /* tree was empty */
    }
  if(comp(n, (*r)) < 0) //comp(n–>u, (*r)–>u)
    insert_node(&L (*r), n);
  else
    insert_node(&R (*r), n);
  return;
}
void traverse_rec(Node *r);
void traverse(){
  Node *r = symtab;
  traverse_rec(r);
}
void traverse_rec(Node *r){
  if(r == NULL)
    return;
  traverse_rec(r–>link[0]);
  display_value(r);
  traverse_rec(r–>link[1]);
}
Node *search_node(Node *r, Node *n){
  if(r == NULL) return NULL;
  if(comp(n, r) < 0) // comp(n–>u, r–>u) < 0
    return search_node(L(r), n);
  else if(comp(n, r) < 0)// comp(n–>u, r–>u) < 0
    return search_node(R(r), n);
else return r;
}