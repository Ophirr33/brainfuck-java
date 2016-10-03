import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

/**
 * An incredibly simple brainfuck interpreter.
 */
public class Interpreter {
  byte[] memory;
  byte[] inputBuffer;
  int head;
  HashMap<Integer, Integer> brackets;

  /**
   * Constructs an Interpreter with the given memory limit
   * @param size The size to set the array length to
   */
  Interpreter(int size) {
    memory = new byte[size];
    inputBuffer = new byte[1];
    head = 0;
    brackets = new HashMap<>();
  }

  /**
   * Constructs an Interpreter with a default size of 30,000
   */
  Interpreter() {
    this(30000);
  }

  /**
   * Preprocess the input to find matching brackets for more efficient
   * loop computation
   * @param input the brainfuck to interpret
   */
  void preProcess(char[] input) {
    brackets = new HashMap<>();
    Stack<Integer> stack = new Stack<>();
    for(int i = 0; i < input.length; i++) {
      char c = input[i];
      if (c == '[') {
        stack.push(i);
      } else if (c == ']' && stack.empty()) {
        throw new IllegalArgumentException("Unexpected right bracket at "+ i);
      } else if (c == ']') {
        int left = stack.pop();
        brackets.put(left, i);
        brackets.put(i, left);
      }
    }
    if (!stack.isEmpty()) {
      StringBuilder bracketIndexes = new StringBuilder(4 * stack.size());
      for(int i : stack) {
        bracketIndexes.append(i).append(" ");
      }
      throw new IllegalArgumentException(
              "Unclosed brackets at " + bracketIndexes.toString());
    }
  }

  /**
   * Convenience method that converts a string to a byte array.
   * @param input The brainfuck code as a string
   * @throws IOException
   */
  void interpret(String input) throws IOException {
    this.interpret(input.toCharArray());
  }

  /**
   * Pre-processes the brainfuck code for better bracket jumps, then
   * interprets it command by command. Note that it wraps increments by default.
   * @param input the branfuck code to interpret
   * @throws IOException
   */
  void interpret(char[] input) throws IOException {
    preProcess(input);
    int i = 0;
    while (i < input.length) {
      switch (input[i]) {
        case '+':
          // add it and modulo 256.
          memory[head] = (byte) Math.floorMod(memory[head] + 1, 256);
          break;
        case '-':
          // substract it and modulo 256.
          memory[head]= (byte) Math.floorMod(memory[head] - 1, 256);
          break;
        case '<':
          if (head == 0) {
            throw new IllegalArgumentException("Header out of bounds: " + head);
          }
          // move the header to the left
          head--;
          break;
        case '>':
          if (head == memory.length - 1) {
            throw new IllegalArgumentException("Header out of bounds: " + head);
          }
          // move the header to the right
          head++;
          break;
        case ',':
          if (System.in.read(inputBuffer) != 1) {
            throw new IOException("Wrong number of bytes read at: " + head);
          }
          // read in user input.
          memory[head] = inputBuffer[0];
          break;
        case '.':
          // print out what's in the box here as a char.
          System.out.print((char)memory[head]);
          break;
        case '[':
          if (memory[head] == 0) {
            // jump to the closing bracket
            i = brackets.get(i);
          }
          break;
        case ']':
          if (memory[head] != 0) {
            // jumps to the opening bracket
            i = brackets.get(i);
          }
          break;
        default: break;
      }
      i++;
    }
  }

  /**
   * Just runs the 99 bottles of beer brainfuck program
   * @param args Ignores these.
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    Interpreter i = new Interpreter();
    i.interpret(
            ("           +++           +++           +++           [>+           +++           ++>           +++           +++           <<- \n" +
                    "    ]>+           ++>           +++           >++           +++           +++           ++>           +++           +++ \n" +
                    "   +++>+         +++++         +++++         [>+++         >++++         >++++         <<<-]         >->>+         +>+++ \n" +
                    "   +++++         [>+++         +++++         ++++>         +++++         +++++         ++>++         +++++         +++++ \n" +
                    "   >++++         +++++         +++>+         +++++         +++++         +>+++         +++++         +++++         >++++\n" +
                    "   +++++         ++++>         +++++         +++++         +++>+         +++++         +++++         ++>++         +++++ \n" +
                    "  ++++++>       +++++++       ++++++>       +++++++       +++++++       >++++++       +++++++       +>+++++       +++++++\n" +
                    "  ++>++++       +++++++       +++>+++       +++++++       ++++>++       +++++++       +++++<<       <<<<<<<       <<<<<<<\n" +
                    "  <-]>+>+       +>++++>       +++++>+       +++++>>       +>+++>+       +++>+++       +++>+++       ++++>>+       +>+++>+ \n" +
                    " +++>+++++     >+++++++<     <<<<<<<<<     <<<<<<<<<     <<<<[>[<<     <.>.>>>>.     >>>>>.>>>     >>>>>>.>>     >>..<<<<<\n" +
                    "<.<<<<<.>>>   >>>>>>>.<<<   <<<<<<<<<<<   <<<.>>>>>>>   >>>>>>>.<<<   <<<.<<<<<<<   <.>>>>>.>>.   .>>>>>>>>>.   <<<<<<<<<<<\n" +
                    "<<<<<.>>>>>   >>>>>>>>>.<   .<<<<<<<<<<   <<<.>>>>>>>   >>>>>>>>>>>   .<<<<<<<<<.   <<.<<<<<<<.   >>>>>>>>>>>   >>>>>>>>>.<\n" +
                    "<<<<<<<<<<<   <<<<.>>>>>>   >>..<<<<<<<   <<<<.<.<<<<   <.>.>>>>.>>   >>>.>>>>>>>   >>.>>>>..<<   <<<<.<<<<<.   >>>>>>>>>>.\n" +
                    "<<<<<<<<<<<   <<<<<<.>>>>   >>>>>>>>>>.   <<<<<<.<<<<   <<<<.>>>>>.   >>..>>>>>>>   >>.<<<<<<<<   <<<<<<<.<.>   >>>>>>>>>>>\n" +
                    ">>>>>>.<<<<   <<<<<<<<<<.   >>>>>>>.<<<   <.<<<<<<<.>   >>>>>>>>>>>   >>.<.<<<<<<   .<<<<<<<.>>   >>>>.>>>>>>   >>.>>>>>>.<\n" +
                    "<<<<<<.<<<<   <<<<<<<<<.>   >>>.>>>>>>>   >>.<<<<<<<.   <<<<<<.>>>>   >>>>>>>>>>>   .<<<<<<<<<<   <.>>>>>>>>>   >>>>..<<<<<\n" +
                    "<<<<<<<<<<<   <.>>>>>>>>>   >.>>>>>>>>.   <<<<<<<<<<<   <<<<<<<.>>>   >.>>>>>>>>>   >>>.<<.>>>>   >.<<<<<<.<<   <<<<<.<<<<<\n" +
                    ".<.<<<<<.>-   .>>>>.>>>>>   .>>>>>>>>>.   >>>>..<<<<<   <.<<<<<.>>>   >>>>>>>.<<<   <<<<<<<<<<<   <<<.>>>>>>>   >>>>>>>.<<<\n" +
                    "<<<.<<<<<<<   <.>>>>>.>>.   .>>>>>>>>>.   <<<<<<<<<<<   <<<<<.>>>>>   >>>>>>>>>.<   .<<<<<<<<<<   <<<.>>>>>>>   >>>>>>>>>>>\n" +
                    " .<< < <<<     <<< . <<.     <<< < <<<     .>> > >>>     >>> > >>>     >>> > >>>     .<< < <<<     <<< < <<<     <<< . >>> \n" +
                    "  >> >>>.       .<<< <<       <<<< <.       << . <<       -]+ +++       ++ + ++       << + ++       ++ + ++       +<->>-]").toCharArray());
  }
}
