import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;

public class UnsupportedExceptionWithModifyAsListRule {
    public void check() {
        List<String> t = Arrays.asList("a","b","c");
        t.add("22");// Noncompliant {{这里使用【add】可能会导致UnsupportedOperationException}}
        t.remove("22");// Noncompliant {{这里使用【remove】可能会导致UnsupportedOperationException}}
        t.clear();// Noncompliant {{这里使用【clear】可能会导致UnsupportedOperationException}}
    }
    public void check2() {
        List<String> t = new ArrayList<String>();
        t.add("22");// Compliant
        t.remove("22");// Compliant
        t.clear();// Compliant
    }
}