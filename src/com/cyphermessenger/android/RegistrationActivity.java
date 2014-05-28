package com.cyphermessenger.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RegistrationActivity extends ActionBarActivity implements View.OnClickListener {

    String uNameFromClient;
    String passwordFromClient;
    String confirmFromClient;
    String captchaTextFromClient;
    String passwordFromServer;

    private EditText uNameField;
    private EditText passwordField;
    private EditText passConfirmField;
    private EditText captchaTextField;
    private ImageView captchaView;
    private Bundle savedState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String captchaEncoded = "iVBORw0KGgoAAAANSUhEUgAAAPoAAAAyCAIAAAD6NVGzAAASvElEQVR42u2de3QUVZ7HWwkCJgbCKzFIgjxiSCKJgTxAXhlZXV0FB4EJjwkmxsFBQQdWRBIfoDGBEARhiYmJURaEGAFjFIyiODo6Ojquro6Ojo6urq6urq6uru9z9nPub3K3Tqf7dlV1J2lovn_0qa6-de_tqs_93d-v6ta9nukdKigo-JnSWWedNWPGjL9TOvvss88555y_Vzr33HPPO--8f1A6__zzL7jggplKs2bNuvDCC3-uNHv27IsuumiO0ty5c-fNm_cLpcLCwvnz5y9QWrhw4aJFi36pVFRUtHjx4ouViouLS0pKLlEqLS299NJLf6W0ZMmSyy677NdKS5cuvfzyy69QWrZs2fLly6_s0FVXXfUbpRUrVqxcufIfla6--upVq1Zdo7R69eprr712jVJZWVl5efl1Stdff_0NN9xwo9LatWvXrVt3k9LNN99cUVFxi1JlZWVVVdV6pQ0bNlRXV29Uqqmp2bRp061Kmzdv3rJly21KW7du3bZt2z8pbd--vba29nalurq6-vr6O5QaGhoaGxvvVGpSukvp7rvv3rFjxz8r7dy5c9euXfco7d69e8-ePc1K9957b0tLy31Ke_fu3bdv336l---_v7W19QGltra2Bx988CGlAwcOHDx48GGl9vb2Rx555FGlQ4cOPfbYY48rHT58-Iknnvit0pNPPvnUU0_9Tunpp59-5plnfq_07LPPPvfcc39Qev7551944YU_Kr344ov_0qGXXnrp5Zdf_lelV1555dVXX_2T0muvvfb666__WemNN9548803_6L01ltvvf32239Veuedd959991_U3rvvffef__9f1f64IMPPvzww_9Q-uijjz7--OP_VPrkk08-_fTT_1L67LPPPv_88_9W-uKLL7788sv_Ufrqq6--_vrr_1Xy2GQ9zHG3w3qY426H9fDH3Q7rRwDuNlkPZ9xtsh7OuNtnPZxxt8P6kYG7TdbDGXf7rIcz7nZYD2fcbbJ-ZOBun_Vwxt0O6-GMu03Wwxl3m6z3JO4kmzZtWmZm5rhx4_Ly8sg5_ONU8uQfkbgrPBnS8OsRHaeGOe7C-jfffOPpzjiV_bm5uYmJiTExMb179-7Vq9dxxx13_PHHR0dHn3766WQlxIdPnEpN8vPz-_fv7_F4qCd_isShwp18ODljxowhZ_n1WJwaFriHJE7l17S0tD59-nj8CPqTk5PJtsfjVH6Ccp-VDN66s8GZ6ZyzU9NOhblGnDFsB6eOT7a5UnRHx-LU7sDdwDopk5KSPPY0YMAAMuxBx52OyFC9IONU_oi_nAPizjY_Ue6IESMMNeRXGs-xOLVncCfZqaee6nEo3Juewp3_bsZdG_hNSo7iVM6SGXeREG_FnV-pLRcCWx7w7EVFRdEqjsWp1ji1O3BnIyMjw84V6iw6BHDv_jh1woQJhlrhzxC84tyTkg2gd-S4T5kyxV_OmOSmpiZCWFLi3AO9xh36cfBSU1OJdmyePc45cfCxOFWz_u2333q6Ok6lOZ144oket-ICc5kx8N0Zp44aNcpQJQ4hk1VKbK9fvx5nxj7uY8eO9ZcziTmWxoNhZgP08WfEtFP_0aNHE9Q6OntDhw7Fg9fER3icagv3YOJUNgYPHmzfIPkUtSopKcHMQ3z3PE8dOHCgoT6USAWk-VFuVVWVI9yHDBniL2dOWm5uLgE9nziBNCSNOyfBEOUbxCkiriV4BXqIj-Q4NZS4-zTtkyZNMrNOh8vVpQ4nnHCCvzRcZvK8-OKLIV4MvBn3lJQUDGF6evrEiROpJOmd3oI0u15EFFSG08W_y8_Pz8rKYpsuyCbuhsyJ0eXmLOrbty_9AG1JcDc0ErOys7MpGr8I6LH04Y9718WpDnBnJ_E-F9gf7iRITk7Gb-FSESdh1DnRAS_SuHHjOJDS4-PjDckyMzNpQkCGVwPxBtwxup0PHzZsmMYdYfLBvbS0NCcnhxrSnGCLCuTl5UnbMNeZ_z5y5EjQlJYsjw6EUWBlm7_fv39_mhznkEKtuBPXOoIVK1BcXMyxvXv39pkgLi6Ogqi8IQG9BA4SpdNshPjIjFMd4G4IsEiAHXLhsURHR89Qog7mO2uxsbE0LX-uhRX3xYsXG_KBZlopf5DW6C8N9JirzbGOfGj-Gj2JmHY2XFjohIQEfzTTq9Bf8Ukn6TMBB9JlVVRUVFdX0-qam5sfeughrHsExqnfffedx2acijfpCbXoDTTu5ugQZWRk-PvJ6sngTZnzkUekwchFw-YQmiu40zjdFeqvgdEXgTuf9Lr-cBc_DeI3btzY0NCwf__-EOJ-BMWpgXHXjvsZZ5wRctwhWHCnAgFtqkFW3KmqJ1xFD4kHFdo88e4Edy6ZzwSDBg3SUTiRAOEETjz-TATGqQ5wNxhX18J1ntGhUOHOH_GEsYhVQpshsQfEE8T7O4Hjx48X1gnBwZ0AuqWlpb29PQLjVAe4n3baaSG_9meeeaYuNKAzY77XJh48xE-ePDmccQ_ynqwL0RK8cBfrHoFxqgPcg7G-_jR9-nQplBJdjDKwXlEZaLB06VLzA9EuVa9evTDeSUlJTh8GWZWSkmKwLE5bC1dWezJOcT_64tS_4W7neaoZx6ioqL59-7q4GBp3wlbX9lK_IVVUVOTO6Ro4cGB8fHxsbKy7wQ7C-uzZs2l1JSUlmAZ3Vnzo0KF4ZYYwibjTZs6k5Nyu61AX4X5kxakBcLc-YDLfKMQVSUtLg3hHV9da7vDhw13jfpHSnDlz5s2b58LpArL5SlTm5JNPdkc85-cyJTqZwsJCd_d_OOeEH1lZWYZGZfPZKrhzNsIH93CIUx3gbra-xEN5eXlxcXHucOdz2LBhru_Q6VEMEI8z4CKE0O8TTp06NSYmpqfcoUsuuQTcMzMzQ-gu6qfFFRUVcmdm7969jz76aATGqSHDfZISZtIp7lIiVwWz6hp3ebgrAxnGjBnjNIeZM2fqt8XJxPx8t0tFwB1a3NHcuXPFtN9yyy01NTWNjY2tra1yWybS4tSQOTNnKiUkJDi6EgUFBVLilClTnDYVn7izMXLkSKc54Hto3BcsWBBM0Byk5F5qaB9x4NWsWbNGnqrW1tY2NzcfPHgwIO5HZZz6_fffe2yO-zVDILgnJiY69SKk0Pz8_EGDBgXvzFBnF06R11wgXXHL1aZkOCeeoc30BEs4b4Q9hNqGMXacYXDfvHnzjh072traDh06FJlxqgl3r4GQ5huRgvspp5zi6Orm5uZKodnZ2bGxsa5DVY375MmTXeRjte6oKx6oOcKdKMhmenqza5WWLFmSk5MTHR3tM1lSUlLP4h4mcaoD3FNTU824g5rTm4np6ekUSgXGjh1rME4BJbjjgpOPi_sqs2bNEtaLiopwZtzhLqM1kTwy81cN8wu7grv9J2UytPN6JRwhf6PE-vXrp52ZPXv2HDhwICTOzBEXpzrA3QyB4O70ySjXftq0afTdxKnBPG6keuCOUXR3-4-aa9znzJnjIthFnLdfd0heQ6HT4NzSceEdcWb45Byaayi42x8HYcW9vLyciN_f7UuZM4dQtb6-vqWl5eGHHwb3SItTHeBujp8Ed6deL243xOO1B_MY0qOGXrm4DWp9xqRx55-6yycmJqbzO7UyKH_ZsmX0P8TQAf-m4E4dbBZK49S4r1q1yl-3QN1k2pzKyspNmzY1NTU98MADBtyP1jj1hx9-8Nh8P9XsUAruXef1mkHRY8FddxEjRoygf8Ae06pdZyLD92k58mZtcXExbhLW3X77kdcOA45htpptLs2KFSvKysporgMGDPCZjPD9ZiWI37BhQ11d3b59-_BnIi1O9Yt75xf28DoC4t4Vg4TRiUqesFHwI-bNuJvfUHHX-2nc169fjwcP7gSskRanOsDdPPUKrHNO_c28FaRyc3NdP3PtCnFCzO9uB4m7z_cPg-kYcajCHPfuiVMd4D5z5kxDLy-4T5061XDe4-PjXfgJqamp4koF6d-HUKWlpQsWLOiKFqjnUAjhOGFOnWadaBVnBtz379-PMxNpcaoz3Pv16xcQd8MoMX7F_Du6L07se74SFXA9ysCfo-9aMn8ln3Q7ThthQkKCYUyOxp1AIiTEp6SkrFu3zhHuR3Gc-uOPP3rsz6OUk5Pj7-pq3P1FSx7P_49WwMUP6IsT9k2cOFFKlwpwIE5zQAioYWZmJmUZWmbAe_yGhq1xlwn9CBCTk5PtoEma4cOH33TTTYZhDlcrXXPNNWvWrPE3T6XNMCYqKorrKKNlQo77ERqn-sbdMLEM9syn26pxNwyb0biL6GRl-grY6qVEz4DtZyftimpo1nUdCJcTExP79Okjs1xI2xPUOJys6A2oucxyU1hY6LMaYEQ9_T2AxPZnZ2fzNw3xqBV3ebOkuLiYo_RUHJ3vn3DSSFBSUlJWVkYzPumkkwLiXllZWV9f3zkNgeyQIUMMDYzisrKybrzxRj2znxX36urquro6cH_88ccjLU51jLsMxiJNenq6GHK6S2FdcOdE6_bABvxNmjTpZx2y4n52h85R0kVL6VIBL9wpnV_BhVJoFVK0nsCs87xOmnuRXlIB8V9Gjx5NS5MpYvikFRFdkDm_wiXEU_NFixbhqetFFmQmbo27HuC-tENs84-oGEDz92mco0aNorfhv_DTypUrVyjRnsePH0992MaBsU5-5oX7rl27GhsbMcnXXXedTJJTXl5OSk714MGD5W0PEeY8Li6OnpNqWI261brLffc777yzra0tfHDvtjjVJe58spOjoI3LZsV9-vTpgAI0IAh2HC6zJllZ18VZ3aeAuFM6eYII_gMQg7XXfH1W3IV4jfv8DmncFy5cyFdKpP5jlGhC1If9spSIdTURn6yLXb_iiivkHdkrr7yS7cstkuUbaDDwp2eA0rPieM3yZ8D9rrvuuvXWW7HWGneZeoCacIomTJiQlpaWkZHBVaA48tcD3PWkaFZPZtu2bc3Nze3t7XJbJqLiVJe4I7ZJVlBQAOVeuJOPPLJB0hWc1SGNOzvJmTzJnFLIyqtcXbSI4sgKdqEQzuCPzOF7dod8WnevdXI06J2XQ5NVokQad6_1c3wusgC12N21a9cCIqTydfny5TKtn5Y8W73KIsEd1olKARe-ORbcZZpVK-67d--G-Ntvv52vMlJApB-mivTsf4b1A2UuMUy7PFKNwDj1b7i7mO8X8RPsgrhmXXAnB8gTa8oGWVlxpzjK4ligJAFEAitFWEu0Fio-DGlICZQQBkB8wiV7hPjOoGuLDtPgC6-wy-G_7JAX4lZZce9s162vgYM1dAJTTU0NhhOwoBBYQVlPxm2d3E-zLnadY0kPl3KUgC7TWbIHOu-44w49s_ttt91WVVVFYoC2wt1ZXrhrl33r1q2wft9992HaDx8-HIFxqg_cbU6Ayic7SY-XQgQ5tUN85XDI04sxadzFXxe7DqACIsLWgqnXHMKaXQS7pMHQwhmsywS8YAevQrxOLK6O5hVYSQZwwh-A8lWHmAIuYg8pOaqzaQd0PcG8TE9JbtoJAVAsq5CEtmzZsnHjRthiJ8iuXr2aZL-xyMo6v0ItBHM4aJIemw2s8toRrJMbbowsTSPEY-zZSdPSC4dIMwNoEO88t6t-YY9khKf33HNPa2ureXDY0R2nusddzCr7Scax5ADNMM0GKaFQfAZAJIEUQbbkRiZwyU-CI4IqINPm1ue6kzL3L6gJMXyyzU7Si9dE42GbxHCpDSopgVJP_ys-g54Le3WHSEP7wXh7OevsEXdFPA0Y5ShQljvZ2hsGQetyHXAPiECM-wF82GlxtSlUGokMY-RwPPLt27dzCPiSmKww4bW1tTKbZFNTE4CCO_a4RQnod-7cebeSLO_BBoGsGH7to4uom7QlfiU369SQETjuNwS4g7JAz35-JTEH8immXXAHRIlrScB-aQZw6bXC3qUdst7d03c8xBX2mu9XvGTSlChJ1EgC4Uk4lil_9fTWXp7u2g7xlcTSaWi3W5oWoHOsvrkhXoGsTiMrdnSe3lqLPSTQNhhbK-WK80PihoYGWa5D1ieTr7Ikk16MybrCniyypzdktT0SgD7FgbWeY17m3aYIQCc3ktl5Ye-oj1ODxd16G0To5xPE9XLBQjyUi5uB_cZyh2r9VCSMioMhZtvFgpKIrzoQ1OlJKfMqykpMeuExmysXWMVOUsqBpGQPlGObsdB67bFdSi4Wg2c_KTsvScmxJNYzXEfmPEpW1n_66SdPCNelkRjRa3VsPZS8i1aCl_t3Qa6favUBKpVkmexqpeCXC9bSC2SHcP1UWVNSb-gF92StDvuzuR_1cao37iFZP9XdYvA9sn5qCBeD75GV4I-tn9oduNtkPZxxt896OONuh_WIXZemu3G3z3o4426H9XDGPWLXpfGJ-_8B-y5DPtk_-3kAAAAASUVORK5CYII";
        byte[] captchaBytes = Base64.decode(captchaEncoded, Base64.URL_SAFE);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.outMimeType = "image/png";
        Bitmap captchaBitmap = BitmapFactory.decodeByteArray(captchaBytes, 0, captchaBytes.length, opt);
        setContentView(R.layout.activity_registration);
        captchaView = (ImageView) findViewById(R.id.captcha_image);
        captchaView.setImageBitmap(captchaBitmap);
        passwordFromServer = "DEBUG";


        // VIEWS
        uNameField = (EditText) findViewById(R.id.name_field);
        passwordField = (EditText) findViewById(R.id.password_field);
        passConfirmField = (EditText) findViewById(R.id.confirm_field);
        captchaTextField = (EditText) findViewById(R.id.captcha_text_field);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        uNameFromClient = uNameField.getText().toString();
        passwordFromClient = passwordField.getText().toString();
        confirmFromClient = passConfirmField.getText().toString();
        captchaTextFromClient = captchaTextField.getText().toString();

        /*
        if(passwordFromClient.length() < 8) {
            Toast.makeText(this.getApplicationContext(), "Invalid password: please insert a password longer than 8 characters", Toast.LENGTH_LONG);
        } else {

        }

        if(passwordFromServer.equals(passwordFromClient)) {

        }*/
    }

    @Override
    protected void onStop() {
        super.onPause();  // Always call the superclass method first
        savedState = new Bundle();
        savedState.putString("userNameClient", uNameFromClient);
        savedState.putString("passwordClient", passwordFromClient);
        savedState.putString("passwordConfirmationClient", confirmFromClient);

    }

    @Override
    protected void onRestart() {
        super.onResume();
        uNameField.setText(savedState.getString("userNameClient"));
        passwordField.setText(savedState.getString("passwordClient"));
        passConfirmField.setText(savedState.getString("passwordConfirmationClient"));
    }
}
