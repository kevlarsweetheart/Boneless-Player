package hellhound.flamingoplayer;

import java.util.ArrayList;

public class UrlNormalizer {
    public ArrayList<Replacer> replacements;

    UrlNormalizer() {
        replacements = new ArrayList<>();
        /*
        replacements.add(new Replacer("&", "%26"));
        replacements.add(new Replacer("?", "%3F"));
        replacements.add(new Replacer("=", "%3D"));
        replacements.add(new Replacer("%", "%25"));
        replacements.add(new Replacer("ё", "ё"));*/
    }

    public String normalize(String str){
        for(Replacer r : replacements){
            str = str.replaceAll(r.original, r.replacement);
        }
        return str;
    }

    private class Replacer{
        String original;
        String replacement;

        public Replacer(String original, String replacement) {
            this.original = original;
            this.replacement = replacement;
        }
    }
}
