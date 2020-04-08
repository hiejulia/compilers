# Compiler architecture/ algorithms
+ Java compiler
+ Crypto compiler 
+ Android compiler
+ GNU Compiler Collection(gcc)

## Principles of compiler design 
<a href="https://imgur.com/XOY6tgi"><img src="https://i.imgur.com/XOY6tgi.png" title="source: imgur.com" /></a>
<a href="https://imgur.com/tosIdXB"><img src="https://i.imgur.com/tosIdXB.png" title="source: imgur.com" /></a>
<a href="https://imgur.com/aDjfjYu"><img src="https://i.imgur.com/aDjfjYu.png" title="source: imgur.com" /></a>
- lexical anaylysis 
<a href="https://imgur.com/7JokU0j"><img src="https://i.imgur.com/7JokU0j.png" title="source: imgur.com" /></a>
<a href="https://imgur.com/1nJeQrf"><img src="https://i.imgur.com/1nJeQrf.png" title="source: imgur.com" /></a>
<a href="https://imgur.com/wosu8XG"><img src="https://i.imgur.com/wosu8XG.png" title="source: imgur.com" /></a>
<a href="https://imgur.com/OXpIAB7"><img src="https://i.imgur.com/OXpIAB7.png" title="source: imgur.com" /></a>

- syntax analysis 
- semantic analysis 
- Intermediate code generation
- Code optimization 
- Code generation phase 
- Symbol table management
- Error handling
- Data-flow analysis 
- Compiler-construction toolkits
- Parsing 
    <a href="https://imgur.com/YhOhF9q"><img src="https://i.imgur.com/YhOhF9q.png" title="source: imgur.com" /></a>
<a href="https://imgur.com/kwW9N08"><img src="https://i.imgur.com/kwW9N08.png" title="source: imgur.com" /></a>
<a href="https://imgur.com/Tgo2tFC"><img src="https://i.imgur.com/Tgo2tFC.png" title="source: imgur.com" /></a>
<a href="https://imgur.com/Qyh0TJb"><img src="https://i.imgur.com/Qyh0TJb.png" title="source: imgur.com" /></a>
<a href="https://imgur.com/YhOhF9q"><img src="https://i.imgur.com/YhOhF9q.png" title="source: imgur.com" /></a>
<a href="https://imgur.com/j6UDrhS"><img src="https://i.imgur.com/j6UDrhS.png" title="source: imgur.com" /></a>



### Java compiler 

### Crypto compiler 




### JVM 
+ Container aware Java 
+ Extended language with more language features (e.g., arrays) for use in ambitious courses.•JVM target for building a compiler that generates code for the Java Virtual Machine (JVM).•Optimizer component and assignment for teaching code optimization.•Interpreter component and assignment for teaching program interpretation.•Alternate build process for building the toolset via Ant [4], in addition to Make [3], en-abling students and instructors to install the toolset on non-Unix platforms such as Windows.
+ Enable execution of Java on GPU 
    + Java Stream API - Lambda - exploit processing power of GPU 
    + 
+ GC 
    + memory allocation 
    + 
+ Core component : 
    + Lexer, Parser, Visitor, AST, Constrainer, Interpreter, Compiler, VM, Debugger, Lexical analysis, Build Abstract syntax, type checking, 
+ Lexx/ Parse generator : JavaCC, JLex, Cup 
+ Build : Make, Ant
+ Advancd : Optimizer, Interpreter 
+ Some proposal 
    + JVM access check 
    + Value objects 
    + Diagnostic command framework 
    + Low overhead way of sampling Java heap allocation 
    + Concurrent monitor delfation 
    + 
+ JVM Tools Interface   
    + Adding patch points or method entry and exit 
    + Enum of compiled methods 
    + State changed notifications and compiled method load 
    + Query support 
    + Chunks of compiled methods 

+ Concurrent JVM 
    + access object 
    + 
+ Lexical issue : + Symbol table (token)
+ Scanner 
+ Dataflow analysis 
+ Program optimization 
+ Code generation 
+ Runtime system 

#### Other examples of Java compiler written in Java (standard java compiler is written in C/C ++)

#### cPLC — A cryptographic programming language and compiler
https://www.researchgate.net/publication/224259131_cPLC_-_A_cryptographic_programming_language_and_compiler
https://cryptography.io/en/latest/installation/
chrome-extension://oemmndcbldboiebfnladdacbdfmadadm/https://eprint.iacr.org/2005/160.pdf
https://crypto.iacr.org/2019/affevents/ppml/page.html



#### Courses (undergraduate/ graduate/ PhD level )
+ Modern Compiler Implementation in Java.
+ Compilers: Principles, Techniques, &Tools
+ Book 
    + Compiling with C# and Java, Pat Terry, 2005, ISBN 032126360X624
    + Compiler Construction, Niklaus Wirth, 1996, ISBN 0-201-40353-6
    + Fischer, C. N. and LeBlanc, R. Jr. "Crafting a Compiler", Benjamin/Cummings, Inc., 1989.


+ Course: 
    
The Java Virtual Machine Specification
Especially: Compiling for the Java Virtual Machine
Jasmin home page
Especially: Jasmin instruction list
LLVM home page
Interactive Illustrations For Inside the Java 2 Virtual Machine
Byte Code Engineering Library
Virtual machine literature
Chapter 15.1 Virtual Machines. In Michael Scott : Programming Language Pragmatics (3rd Ed.). Morgan Kaufmann, USA, 2009.
Chapter 10 Intermediate Representations & Chapter 11 Code Generation for a Virtual Machine. In Fischer C.N., Cytron R.K. & LeBlanc R.J.Jr. Crafting A Compiler. Addison-Wesley, 2010.
Iain Craig, Virtual machines. London, Springer-Verlag, 2006.
Bill Blunden, Virtual machine design and implementation in C/C++. Plano, Texas, Wordware Publishing, 2002.
Joshua Engel, Programming for the JAVA virtual machine. Reading (Massachusetts), Addison-Wesley, 1999.
Tim Lindholm, Frank Yellin, The Java virtual machine specification. Reading (MA) : Addison-Wesley, 1999.
Bill Venners, Inside the Java virtual machine. New York (NY), McGraw-Hill, 1998.
Jon Meyer and Troy Downing, Java virtual machine. O’Reilly, 1997
Gamma et al.: Design Patterns: Elements of Reusable Object-Oriented Software.  Addison-Wesley, Reading, 1995.

+ Resource :
    + http://www.cse.chalmers.se/edu/year/2018/course/TDA283_Compiler_Construction/project/
    + chrome-extension://oemmndcbldboiebfnladdacbdfmadadm/http://www.cse.chalmers.se/edu/year/2016/course/TDA283_Compiler_Construction/proj.pdf




+ Progress: 
    + https://www.cs.rochester.edu/~cding/Teaching/compilerProjects.html
        + done : scanner 
