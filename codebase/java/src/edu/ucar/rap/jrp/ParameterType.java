// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
// ** Copyright UCAR (c) 1992 - 2012 
// ** University Corporation for Atmospheric Research(UCAR) 
// ** National Center for Atmospheric Research(NCAR) 
// ** Research Applications Laboratory(RAL) 
// ** P.O.Box 3000, Boulder, Colorado, 80307-3000, USA 
// ** 2012/9/14 15:38:13 
// *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* 
package edu.ucar.rap.jrp;

/*******************************************************************
 * Type-safe enum for parameter types
 * 
 * @author Mike Dixon
 */

public class ParameterType

{
    
  public final String name;

  private ParameterType(String name) { this.name = name; }

  public String toString() { return name; }

  public static final ParameterType STRING = new ParameterType("String");
  public static final ParameterType BOOLEAN = new ParameterType("Boolean");
  public static final ParameterType INTEGER = new ParameterType("Integer");
  public static final ParameterType FLOAT = new ParameterType("Float");
  public static final ParameterType DOUBLE = new ParameterType("Double");
  public static final ParameterType OPTION = new ParameterType("Option");
  public static final ParameterType COLOR = new ParameterType("Color");
  public static final ParameterType FILE = new ParameterType("File");
  public static final ParameterType COLLECTION = new ParameterType("Collection");

  public static final ParameterType STRING_ARRAY =
    new ParameterType("StringArray");
  public static final ParameterType BOOLEAN_ARRAY =
    new ParameterType("BooleanArray");
  public static final ParameterType INTEGER_ARRAY =
    new ParameterType("IntegerArray");
  public static final ParameterType FLOAT_ARRAY =
    new ParameterType("FloatArray");
  public static final ParameterType DOUBLE_ARRAY =
    new ParameterType("DoubleArray");
  public static final ParameterType OPTION_ARRAY =
    new ParameterType("OptionArray");
  public static final ParameterType COLOR_ARRAY =
    new ParameterType("ColorArray");
  public static final ParameterType FILE_ARRAY =
    new ParameterType("FileArray");
  public static final ParameterType COLLECTION_ARRAY =
    new ParameterType("CollectionArray");
    
}
