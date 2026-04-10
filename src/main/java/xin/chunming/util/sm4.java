package xin.chunming.util;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import xin.chunming.Main;


import java.io.IOException;
import java.net.URL;

public class sm4 {
    public static String parse(String str) throws IOException {

        Context ctx = Context.create();
        URL url = Main.class.getResource("/encode.js");
        ctx.eval(Source.newBuilder("js", url).build());

        Value SM4Ut = ctx.getBindings("js").getMember("SM4Util");
        Value sm4 = SM4Ut.newInstance();

        Value encrypted = sm4.getMember("encryptDefault_CBC").execute(str);
        return encrypted.asString();


    }
}
