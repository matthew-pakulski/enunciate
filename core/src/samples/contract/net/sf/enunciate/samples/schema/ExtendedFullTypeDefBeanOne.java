package net.sf.enunciate.samples.schema;

/**
 * @author Ryan Heaton
 */
public class ExtendedFullTypeDefBeanOne extends FullTypeDefBeanOne {

  private String property6;

  public String getProperty6() {
    return property6;
  }

  public void setProperty6(String property6) {
    this.property6 = property6;
  }

  public static class NestedExtendedFullTypeDefBeanOne {

    private String nestedProperty;

    public String getNestedProperty() {
      return nestedProperty;
    }

    public void setNestedProperty(String nestedProperty) {
      this.nestedProperty = nestedProperty;
    }
  }
}
