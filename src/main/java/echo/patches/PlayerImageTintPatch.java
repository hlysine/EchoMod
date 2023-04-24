package echo.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "renderPlayerImage"
)
public class PlayerImageTintPatch {
    public static ExprEditor Instrument() {
        return new ExprEditor() {
            public void edit(MethodCall m)
                    throws CannotCompileException {
                if (m.getClassName().equals("com.badlogic.gdx.graphics.g2d.SpriteBatch") && m.getMethodName().equals("setColor"))
                    m.replace("{ sb.setColor(this.tint.color); }");
            }
        };
    }
}
