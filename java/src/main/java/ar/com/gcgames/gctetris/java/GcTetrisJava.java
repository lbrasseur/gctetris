package ar.com.gcgames.gctetris.java;

import playn.core.PlayN;
import playn.java.JavaPlatform;

import ar.com.gcgames.gctetris.core.GcTetris;

public class GcTetrisJava {

  public static void main(String[] args) {
    JavaPlatform.Config config = new JavaPlatform.Config();
    // use config to customize the Java platform, if needed
    JavaPlatform.register(config);
    PlayN.run(new GcTetris());
  }
}
