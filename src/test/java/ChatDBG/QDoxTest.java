package ChatDBG;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

import java.io.File;
import java.util.List;

public class QDoxTest {
    // Reference: https://www.zhihu.com/question/428654696/answer/1558114385
    // Target: Retrieve the code for the given function and class
    public static void main(String[] args){
        String targetPath = Constants.getInstance().basedir + "/src/test/java";
        String[] parts = Constants.getInstance().testEntryClass.split("\\.");
        for (int i = 0; i < parts.length - 1; i++) {
            targetPath += "/" + parts[i];
        }

        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File(targetPath));

        JavaClass cls = builder.getClassByName(Constants.getInstance().testEntryClass);
        List<JavaMethod> methods = cls.getMethods();
        String targetMethodName = Constants.getInstance().testEntryMethod;
        for (JavaMethod method : methods) {
            if (!method.getName().equals(targetMethodName)) {
                continue;
            }
            System.out.println(method.getName());
            System.out.println(method.getCodeBlock());
        }
    }
}
