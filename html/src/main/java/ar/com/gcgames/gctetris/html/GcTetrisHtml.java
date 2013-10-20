package ar.com.gcgames.gctetris.html;

import playn.core.PlayN;
import playn.html.HtmlGame;
import playn.html.HtmlPlatform;

import ar.com.gcgames.gctetris.core.GcTetris;

public class GcTetrisHtml extends HtmlGame {

  @Override
  public void start() {
    HtmlPlatform.Config config = new HtmlPlatform.Config();
    // use config to customize the HTML platform, if needed
    HtmlPlatform platform = HtmlPlatform.register(config);
    platform.assets().setPathPrefix("gctetris/");
    PlayN.run(new GcTetris());
  }
}
