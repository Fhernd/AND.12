package co.ortizol;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Representa la pantalla de inicio de sesión.
 */
public class LogoSplashScreenActivity extends AppCompatActivity implements Runnable{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        goToMainActivity();
        finish();
    }

    /**
     * Inicia la actividad principal de la aplicación después de 2 segundos de espera.
     */
    private void goToMainActivity() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
