package cmm;
public class CMM {

  /** Main entry point. */
  public static void main(String args[]) {
	  CMMParser parser;
	  String file = "cmm/fact.spl";
      System.out.println("CMM Interpreter Version 0.1:  Reading from file " +file+ " . . .");
      try {
        parser = new CMMParser(new java.io.FileInputStream(file));
      } catch (java.io.FileNotFoundException e) {
        System.out.println("CMM Interpreter Version 0.1:  File " +file + " not found.");
        return;
      }

    try {
      parser.Start();
      //parser.jjtree.rootNode().interpret();
     ((SimpleNode)parser.rootNode()).dump("\t");
    } catch (ParseException e) {
      System.out.println("CMM Interpreter Version 0.1:  Encountered errors during parse.");
      e.printStackTrace();
    } catch (Exception e1) {
      System.out.println("CMM Interpreter Version 0.1:  Encountered errors during interpretation/tree building.");
      e1.printStackTrace();
    }
  }
}
