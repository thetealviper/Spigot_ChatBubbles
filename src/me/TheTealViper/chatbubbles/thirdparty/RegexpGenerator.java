package me.TheTealViper.chatbubbles.thirdparty;

public class RegexpGenerator {
  /*
   * Full credit for this code goes to @A4Paper's ChatFilter
   * https://github.com/A4Papers/ChatFilter
   * I modified it with what I argue to be improvements, but the foundation
   * was entirely stripped from his GitHub project.
   * My code specifically handles when filtered words end in a repeated
   * character better as the original code erroneously would match into
   * the next safe word in certain circumstances.
   */
	
  public String generateRegexp(String s) {
    StringBuilder stringBuilder = new StringBuilder();
    String lastChars = "";
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      String chars = String.valueOf(c);
      if (chars.equals(" ")) {
        String reg = "(%SPACE%+)+(\\W|_)*";
        stringBuilder.append(reg);
      } else if (chars.equals("#")) {
        String reg = "#";
        stringBuilder.append(reg);
      } else if (chars.equals("[") || chars.equals("]") || chars.equals("(") || chars.equals(")")) {
    	  String reg = "(\\" + chars + ")+(\\W|_)*";
    	  stringBuilder.append(reg);
      } else {
		if (!(i == s.length() - 1 && lastChars.equals(chars))) { //If last letter is repeated, don't include twice (ex. butt should be considered but or else checks into next word typed so 'butt test' breaks)
		  String reg = "(" + c + ")+(\\W|_)*";
          stringBuilder.append(reg);
	    } else {
	    	//Find last "+" and replace with "{2,}"
	    	int lastPlusIndex = stringBuilder.toString().lastIndexOf('+');
	    	stringBuilder.replace(lastPlusIndex, lastPlusIndex+1, "{2,}");
	    }
      }
      lastChars = chars;
    } 
    String regexString = toLeetSpeak(stringBuilder.toString().replaceAll("\\s", "").replaceAll("%SPACE%", " "));
    if (!regexString.contains("#")) {
      regexString = "(" + regexString + ")";
    } else if (regexString.startsWith("#") && regexString.endsWith("#")) {
      regexString = regexString.replace("#", "");
      regexString = "\\b(" + regexString + ")\\b";
    } else if (regexString.startsWith("#")) {
      regexString = regexString.replace("#", "\\b(");
      regexString = regexString + ")";
    } else if (regexString.endsWith("#")) {
      regexString = regexString.replace("#", ")\\b");
      regexString = "(" + regexString;
    } 
    return regexString;
  }
  
  String toLeetSpeak(String speak) {
	StringBuilder sb = new StringBuilder(speak.length());
	for (char c : speak.toCharArray()) {
	  switch (c) {
	  case 'a':
	    sb.append("@|a|4");
	    break;
	  case 'b':
	    sb.append("8|b");
	    break;
	  case 'c':
	    sb.append("\\(|c");
	    break;
	  case 'd':
	    sb.append("d");
	    break;
	  case 'e':
	    sb.append("3|e");
	    break;
	  case 'f':
	    sb.append("f");
	    break;
	  case 'g':
	    sb.append("6|g");
	    break;
	  case 'h':
	    sb.append("h");
	    break;
	  case 'i':
	    sb.append("!|i|1");
	    break;
	  case 'j':
	    sb.append("j");
	    break;
	  case 'k':
	    sb.append("k");
	    break;
	  case 'l':
	    sb.append("1|l");
	    break;
	  case 'm':
	    sb.append("m");
	    break;
	  case 'n':
	    sb.append("n");
	    break;
	  case 'o':
	    sb.append("0|o");
	    break;
	  case 'p':
	    sb.append("p");
	    break;
	  case 'q':
	    sb.append("q");
	    break;
	  case 'r':
	    sb.append("r");
	    break;
	  case 's':
	    sb.append("\\$|s|5");
	    break;
	  case 't':
	    sb.append("7|t");
	    break;
	  case 'u':
	    sb.append("u");
	    break;
	  case 'v':
	    sb.append("v");
	    break;
	  case 'w':
	    sb.append("w");
	    break;
	  case 'x':
	    sb.append("x");
	    break;
	  case 'y':
	    sb.append("y");
	    break;
	  case 'z':
	    sb.append("2|z");
	        break;
	      default:
	        sb.append(c);
	        break;
	  } 
	}
	return sb.toString();
  }
  
}