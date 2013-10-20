package ar.com.gcgames.gctetris.android;

import playn.android.GameActivity;
import playn.core.PlayN;

import ar.com.gcgames.gctetris.core.GcTetris;

public class GcTetrisActivity extends GameActivity {

  @Override
  public void main(){
    PlayN.run(new GcTetris());
  }
}
