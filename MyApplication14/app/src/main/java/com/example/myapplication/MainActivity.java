package com.example.myapplication;

/*
# Update
2024/09/30

# Title
Zebra 値下げラベル発行アプリケーション v0.02

# System Requirement
(GitHub Address)

# 備考
- 後進のため、可読性の高いコードに念頭をおいて開発。
- 上記より、読み手によっては少し冗長的の可能性あり。
- 展示会・デモで映えるUI/デザインも次Ver以降の課題。

 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.PendingIntent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;

import com.zebra.sdk.comm.BluetoothConnectionInsecure;
import com.zebra.sdk.comm.Connection;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    // 商品マスター
    // 次のVersionでCSV化を検討
    String[][] itemList=
            {
                    {"4904230073888", "未来のレモンサワー", "400", "2024/04/01", "0"},
                    {"4009041108702", "Kries コーヒー", "200", "2024/02/01", "0"},
                    {"4902688165322", "ハチ食品 激辛スパイス", "202", "2024/02/02", "0"},
                    {"4902470020488", "フェザー剃刀S", "100", "2024/08/02", "0"},
                    {"4902410266419", "北海道バタースフレ", "119", "2024/10/13", "4"},
                    {"4902410267003", "大福ホイップアンパン", "119", "2024/10/13", "4"},
                    {"4903110098867", "ルヴァンバターロール", "139", "2024/10/14", "3"},
                    {"4903110099901", "ルヴァンレーズンロール", "139", "2024/10/14", "3"},
                    {"4902410513155", "フジパン 森の切り株", "139", "2024/10/13", "6"}
            };

//    // backup
//    String[][] itemList=
//            {
//                    {"4904230073888", "未来のレモンサワー", "400", "2024/04/01"},
//                    {"4009041108702", "Kries コーヒー", "200", "2024/02/01"},
//                    {"4902688165322", "ハチ食品 激辛スパイス", "202", "2024/02/02"},
//                    {"4902470020488", "フェザー剃刀S", "100", "2024/08/02"},
//                    {"", "", "", "2024//"},
//                    {"", "", "", "2024//"},
//                    {"", "", "", "2024//"},
//                    {"", "", "", "2024//"},
//                    {"", "", "", "2024//"}
//            };

    String sZplFormat = "^XA^DFE:demo01.ZPL^FS^CI27^PW400^LL300^LS0^FO19,73^GFA,201,300,12,:Z64:eJxjYCAMGA8wMLAxMPOA2PX/5f+z/38sg8SWfwBVw2PGbAFi2/8Dsfn3HoCyef+ff3sArMb+/9xtx/kbwHohbIg5DAm2247PBalhf8aSIrvt/I8PQLZdHlse77bT9iAz/9nwgdgGIDV5NjLpvNsOg9l8/ySSe/9D2PbPNz88+/+wPZjNrHyQtwEiTi0AAGNbQmM=:FF7E^FO156,88^GFA,97,200,8,:Z64:eJxjYMAP6v8zNyDThcft/6HSbAwQmv8gRJ38+wYwbVMH4hewV36A0MpQms0AQvPAaLD+Ann+/2Bajvk/ASdhBQABrBnx:F7C8^FO109,137^GFA,197,300,12,:Z64:eJxjYCAKsCOY9s8ZCiAsGwkEu86AIZ3/f///A0B2moH8cxj7WYL8cwYLBhsGMFuCXf7/gYIGkJp7FiB2EkhN/X6gOTwMhx2AbNvN9s+Z/zEcBKlnNt6Qzv6f4QCE3ZDOxwZhsxkzpPMwMDwAs+0Z0vkYIK7gkWN/DlRv3wBx8nHm/wz8DcT5jygAALq2LKg=:A2E1^FO218,73^GFA,285,400,16,:Z64:eJxjYCAHNDAwNjAcBrEkLOz/9/A58zD+/3EcxJf/b/9/Dj87TyOQ/wMsz3BAhofNspm34ZwFkG/+n+GA5P+5xc28ze8LQPznDA8k/3fvP1z/+TeU/0OCh1nueO7Gww+A/PL/DP8keNj/PwfyD4D4zxnOAfk1+rkbn4PUf3/OcEiKh9/+ee7mfzYQ9QekeeR/Hs7ddP4HxLwDcjz8d4H8MwYwvgQ7f3PuBggfqJ7HAsiv/wDhMwD5Bez2n+s/nP8AlWdQYH+QnGAAlqc1AAADOlr4:F6E3^FO236,99^GFA,173,200,8,:Z64:eJxjYMAJ2CSABB/jATYJGQYGGcYDfBJ8DAwSjAf4/x9nYDCo/y///zCYlrFhZmBIYH5gY8PYAKZrQDT7h5o4EM3/Q+cWiJa9w3MbRFue5/3fCKTLv/fXH2xgMHD/yC/RDDSH8QCPBNAcCxDNDrQP5AI+MIJy8AMABh4jzA==:F47B^FO349,99^GFA,49,100,4,:Z64:eJxjYEAF9f8huPA4KoaJF7BjwfJALMeAEwAAd9UOSQ==:ED0B^FO351,137^GFA,49,100,4,:Z64:eJxjYEAF9f8huPA4KoaJF7BjwfJALMeAEwAAd9UOSQ==:ED0B^FO236,137^GFA,165,200,8,:Z64:eJxjYMAP+PiPCf6QYWCQqThm+YOPgUFC4pyMBDsDg4Hkfw4LZiBd38wAohPqm21sGBsYEiShtO1/mTwQbfNGJg1E172ROQ6iK87ObW4E0hFn5zMC+QaSZ/cxgMyR7P/+/zADg4U0v+L/dqB9YMvBCMrBDwByGiO0:830C^FO19,95^GFA,173,200,8,:Z64:eJxjYMAJ2CSABB/jATYJGQYGGcYDfBJ8DAwSjAf4/x9nYDCo/y///zCYlrFhZmBIYH5gY8PYAKZrQDT7h5o4EM3/Q+cWiJa9w3MbRFue5/3fCKTLv/fXH2xgMHD/yC/RDDSH8QCPBNAcCxDNDrQP5AI+MIJy8AMABh4jzA==:F47B^BY2,3,48^FT51,223^BCN,,N,N,,A^FN1^FDBarcodeDataFull^FS^FT212,245^A0N,17,18^FH\\^CI28^FN2^FDBarcodeData02^FS^CI27^FT331,244^A0N,17,18^FH\\^CI28^FN3^FDBarcodeData04^FS^CI27^FT256,244^A0N,17,18^FH\\^CI28^FN4^FDBarcodeData03^FS^CI27^FT100,108^A0N,43,38^FH\\^CI28^FN5^FDPriceOrg^FS^CI27^FT72,156^A0N,39,35^FH\\^CI28^FN6^FDDiscountRate^FS^CI27^FT288,124^A0N,33,38^FH\\^CI28^FN7^FDPriceNew^FS^CI27^FT288,158^A0N,26,43^FH\\^CI28^FN8^FDPriceNewWTax^FS^CI27^FT49,246^A0N,17,18^FH\\^CI28^FN9^FDBarcodeData01^FS^CI27^XZ";



    String sBarcodeCheckDigit = "9";
    String sPrtBtMac = "";
    String sLogtag = "AAA";     // Log.v, logger のフラグ、テスト用
    String stvLogs = "";        // tvLogs 用
    //String sZplData = "^XA^FO20,20^A0N,25,25^FDThis is a ZPL test.^FS^XZ";

    int TAXRATE_LOW = 8;
    int TAXRATE_HIGH = 10;
    int iTaxRate = 8;           // 消費税税率

    Boolean bNFCPrintMode = false;   // NFC Print Mode
    Boolean bScanPrintMode = false; // Scan Print Mode
    Boolean bFastPrintMode = false; // Fast Print Mode

    Map<String, String> prtInfo = new HashMap<>();  // NDEF格納用
    // Map data 例、
    // {Wireless=000000000000, Bluetooth=5cf8218dc99b, Serial=50J163001771, Ether=00074d6c9e65, SKU=ZD41H23-D0PE00EZ}

    // NFC タッチ監視用
    PendingIntent pendingIntent;

    // NDEF インテント フィルタを宣言
    IntentFilter iFilterNDEF = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
    IntentFilter iFilterTECH = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

    IntentFilter[] intentFiltersArray = new IntentFilter[]{iFilterNDEF};

    // 処理するタグ テクノロジーの配列
    String[][] techListsArray = new String[][]{new String[]{Ndef.class.getName()}};

    // NFC Adapter 宣言
    NfcAdapter nfcAdapter;

    // NFC Tag 宣言
    Tag tagFromIntent;

    // プリンタとのBluetooth 接続用
    Connection thePrinterConn = new BluetoothConnectionInsecure("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 各オブジェクトをインスタンス化
        TextView tvTargetPrinter =  findViewById(R.id.tvTargetPrinter);
        TextView tvLogs =findViewById(R.id.tvLogs);
        TextView tvItemCode =findViewById(R.id.tvItemCode);
        TextView tvItemName =findViewById(R.id.tvItemName);
        TextView tvItemPrice =findViewById(R.id.tvItemPrice);
        TextView tvItemDate =findViewById(R.id.tvItemDate);
        TextView tvPrtAmount =findViewById(R.id.tvPrtAmount);
        TextView tvDiscountRate =findViewById(R.id.tvDiscountRate);

        EditText edTargetPrinter =  findViewById(R.id.edTargetPrinter);
        EditText edItemCode =  findViewById(R.id.edItemCode);
        EditText edItemName =  findViewById(R.id.edItemName);
        EditText edItemPrice =  findViewById(R.id.edItemPrice);
        EditText edItemDate =  findViewById(R.id.edItemDate);
        EditText edPrtAmount =  findViewById(R.id.edPrtAmount);
        EditText edDiscountRate =  findViewById(R.id.edDiscountRate);

        Button btnPrint = findViewById(R.id.btnPrint);

        ToggleButton tbTax = findViewById(R.id.tbTax);
        ToggleButton tbNfcMode = findViewById(R.id.tbNfcMode);
        ToggleButton tbScanMode = findViewById(R.id.tbScanMode);
        ToggleButton tbFastMode = findViewById(R.id.tbFastMode);


        // 各オブジェクトのラベルを設定
        tvTargetPrinter.setText(R.string.tvTargetPrinter);
        tvItemCode.setText(R.string.tvItemCode);
        tvItemName.setText(R.string.tvItemName);
        tvItemPrice.setText(R.string.tvItemPrice);
        tvItemDate.setText(R.string.tvItemDate);
        tvPrtAmount.setText(R.string.tvPrtAmount);
        tvDiscountRate.setText(R.string.tvDiscountRate);
        tvLogs.setText(R.string.msgtvLogDefault);

        edTargetPrinter.setText(R.string.edTargetPrinter);
        edItemCode.setText(R.string.blank);
        edItemName.setText(R.string.blank);
        edItemPrice.setText(R.string.blank);
        edItemDate.setText(R.string.blank);
        edDiscountRate.setText(R.string.edDiscountRate);
        edPrtAmount.setText(R.string.edPrtAmount);

        btnPrint.setText(R.string.btnPrint);



        tbTax.setTextOff(getResources().getString(R.string.tbTaxOff));
        tbTax.setTextOn(getResources().getString(R.string.tbTaxOn));

        tbNfcMode.setTextOff(getResources().getString(R.string.tbNfcModeOff));
        tbNfcMode.setTextOn(getResources().getString(R.string.tbNfcModeOn));

        tbScanMode.setTextOff(getResources().getString(R.string.tbScanModeOff));
        tbScanMode.setTextOn(getResources().getString(R.string.tbScanModeOn));

        tbFastMode.setTextOff(getResources().getString(R.string.tbFastModeOff));
        tbFastMode.setTextOn(getResources().getString(R.string.tbFastModeOn));


        // 印刷ボタン押下時の処理
        btnPrint.setOnClickListener(view -> {
            // Zplの生成と選択されたモードで印刷
            createZplAndSelectPrintMode();
        });


        // Scan mode ON/OFF 設定
        tbScanMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // The toggle is enabled
                addTvLogs(getResources().getString(R.string.msgScanModeOn), true);
                bScanPrintMode = true;
            } else {
                // The toggle is disabled
                addTvLogs(getResources().getString(R.string.msgScanModeOff), true);
                bScanPrintMode = false;
            }
        });

        // NFC mode ON/OFF 設定
        tbNfcMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // The toggle is enabled
                addTvLogs(getResources().getString(R.string.msgNfcModeOn), true);
                bNFCPrintMode = true;
            } else {
                // The toggle is disabled
                addTvLogs(getResources().getString(R.string.msgNfcModeOff), true);
                bNFCPrintMode = false;
            }
        });

        // Fast mode ON/OFF 設定
        tbFastMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // The toggle is enabled
                addTvLogs(getResources().getString(R.string.msgFastModeOn), true);
                bFastPrintMode = true;
            } else {
                // The toggle is disabled
                addTvLogs(getResources().getString(R.string.msgFastModeOff), true);
                bFastPrintMode = false;

                // Close Bluetooth
                prtBtClose();

            }
        });


        // ToggleBox-Tax, 消費税率の切り替え設定
        tbTax.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // The toggle is enabled
                //tvLogs.setText(R.string.msgTax10);
                addTvLogs(getResources().getString(R.string.msgTax10), true);
                iTaxRate = TAXRATE_HIGH;
            } else {
                // The toggle is disabled
                addTvLogs(getResources().getString(R.string.msgTax08), true);
                iTaxRate = TAXRATE_LOW;
            }
        });


        // DataWedge - IntentFilter, Broadcast Receiverの宣言
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));
        registerReceiver(myBroadcastReceiver, filter);


        /*
        NFC コードの実装
         */

        // NfcAdapterの宣言
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // NFC機能が実装されていない機器に対する配慮
        if (nfcAdapter == null) {
            Toast.makeText(this, "NO NFC Capabilities",
                    Toast.LENGTH_SHORT).show();
            finish();
        }


        // Foreground Dispatch
        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE);


        try {
            iFilterNDEF.addDataType("*/*");    /* Handles all MIME based dispatches.
                                       You should specify only the ones that you need. */
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

    }

    /*
    TextView TvLogs反映処理
    clear = true; 新規
    clear = false; 追加
     */
    private void addTvLogs(String msg, Boolean clear){
        String newMsg = "";
        TextView tvLogs =findViewById(R.id.tvLogs);

        // tvLogsの初期化
        if (clear) { tvLogs.setText("");}

        // 反映msgの作成
        newMsg = tvLogs.getText() + "\n" + msg;
        tvLogs.setText(newMsg);

    }



    /*
    ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    スキャナ処理 (DataWedge)
    ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
     */

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }

    // BroadCast Receiverの処理 (DataWedge)
    //
    // After registering the broadcast receiver, the next step (below) is to define it.
    // Here it's done in the MainActivity.java, but also can be handled by a separate class.
    // The logic of extracting the scanned data and displaying it on the screen
    // is executed in its own method (later in the code). Note the use of the
    // extra keys defined in the strings.xml file.
    //
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();

            //  This is useful for debugging to verify the format of received intents from DataWedge
            //for (String key : b.keySet())
            //{
            //    Log.v(LOG_TAG, key);
            // logger("String key : b.keySet(): " + b.keySet());
            //}

            if (action.equals(getResources().getString(R.string.activity_intent_filter_action))) {
                //  Received a barcode scan
                try {
                    displayScanResult(intent, "via Broadcast");
                } catch (Exception e) {
                    //  Catch if the UI does not exist when we receive the broadcast
                }
            }
        }
    };

    // スキャナ処理 - Datawedge
    // スキャン時の処理はここに追加する
    //
    // The section below assumes that a UI exists in which to place the data. A production
    // application would be driving much of the behavior following a scan.
    //
    private String displayScanResult(Intent initiatingIntent, String howDataReceived)
    {
        String decodedSource = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_source));
        String decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
        String decodedLabelType = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type));

        logger("decodedSource: " + decodedSource);
        logger("decodedData: " + decodedData);
        logger("decodedLabelType");

        addTvLogs("スキャナ：" + "\t" + decodedSource, true);
        addTvLogs("データ：" + "\t" + decodedData, false);
        addTvLogs("バーコード：" + "\t" + decodedLabelType, false);

        // 各EditViewにデータを追加
        refreshItemData(decodedData);


        // Zplの生成と選択されたモードで印刷
        if (bScanPrintMode==true){
            createZplAndSelectPrintMode();
        }

        return decodedData;
    }

    // Janデータを基に付帯データを取得
    private String[] getItemData(String jan){

        // デモアプリのため、マスタデータは直書き
        // マッチデータがない場合のデータ
        String[] returnList = {jan,"No Data","No Data","No Data"};

        // Janのマッチング
        for (int i=0; i<itemList.length; i++){

            // Equality判定ができないため、indexof で判定
            // if (itemList[i][0] == jan) {
            //
            // 念のためということであればstring.Length==13判定を追加する

            if (jan.indexOf(itemList[i][0]) == 0){
                returnList = itemList[i];
            }
        };

        return returnList;
    }

    // decodeDataを基に表示データを更新
    private void refreshItemData(String jan){
        EditText edItemCode =  findViewById(R.id.edItemCode);
        EditText edItemName =  findViewById(R.id.edItemName);
        EditText edItemPrice =  findViewById(R.id.edItemPrice);
        EditText edItemDate =  findViewById(R.id.edItemDate);

        String[] itemList = getItemData(jan);
        edItemCode.setText(jan);
        edItemName.setText(itemList[1]);
        edItemPrice.setText(itemList[2]);
        edItemDate.setText(itemList[3]);

        // Z様向け処理
        // 割引バーコード末の１桁対応
        // ロジックがわからないので実際のラベルど同じ値が入るようにする
        // データはDBから引いてくる
        sBarcodeCheckDigit = itemList[4];
    }




    private void logger(String str){
        Log.v(sLogtag, str);
    }

    /*
    ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    NFC関連処理
    ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
     */

    @Override
    public void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        assert nfcAdapter != null;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
        // nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    // NFC 読み取り時はonNetIntentが起動
    // 読み取り時の処理はここに記載
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        // テスト用： 各種タグ情報の取得
        getTAGinformation(intent);

        /*
        // Payloadの読み取り(Pageは４ページ単位で取得可能)
        String payload1Page;
        payload1Page = readTagPage(tagFromIntent, 4);
        payload1Page += readTagPage(tagFromIntent, 8);
        payload1Page += readTagPage(tagFromIntent, 12);
        Log.v(sLogtag, "Payload-1st page: " + payload1Page);
         */

        // Mifare UltralightのNDEF の読み取り
        //  必要な部分のみ収集する
        int startPage = 16;
        int endPage = 32;

        String payloadRange = readTagPageRange(tagFromIntent, startPage, endPage);
        logger("Payload-Range: " + payloadRange);


        // Link-OS Printer 情報の収集
        prtInfo = getPrinterInfo(payloadRange);

        // テスト用
        logger(prtInfo.toString());

        // ログの追加
        addTvLogs("Bluetooth MAC is " + prtInfo.get("Bluetooth"), true);
        addTvLogs("Serial# is " + prtInfo.get("Serial"), false);
        addTvLogs("SKU is " + prtInfo.get("SKU"), false);

        EditText edTargetPrinter =  findViewById(R.id.edTargetPrinter);
        edTargetPrinter.setText(prtInfo.get("Serial"));

        // Bluetooth アドレスの格納
        sPrtBtMac = prtInfo.get("Bluetooth");

        // NFCタッチモードがtrueの場合 > 印刷処理を実行
        if (bNFCPrintMode == true){
            // Zplの生成と選択されたモードで印刷
            createZplAndSelectPrintMode();
        }

    }


    // NFC
    // Link-OS プリンタから特定ページデータの抽出
    // Link-OS Printer NDEF Message 例、
    //　�p��lU�zebra.com/apps/r/nfc?mE=000000000000&mW=ac3fa449af4c&mB=ac3fa449af4d&c=ZQ51-AUN010A-00&s=XXRAJ151700742&v=0�
    //  �p��lU�zebra.com/apps/r/nfc?mE=00074d6c9e65&mW=000000000000&mBL=5cf8218dc99b&c=ZD41H23-D0PE00EZ&s=50J163001771&v=0�
    //
    //    データ構造
    //    �p��lU�zebra.com/apps/r/nfc
    //    1. ?mE=000000000000      Ethernet Mac
    //    2. &mW=ac3fa449af4c      Wi-Fi Mac
    //    3. &mB=ac3fa449af4d      Bluetooth Mac（Classic）
    //    3. &mBL=5cf8218dc99b     Bluetooth Mac（LE）
    //    4. &c=ZQ51-AUN010A-00    SKU
    //    5. &s=XXRAJ151700742     Serial #
    //    6. &v=0�                 Version

    public Map<String, String> getPrinterInfo(String payload) {
        Map<String, String> prtInfo = new HashMap<>();

        // Blutooth Classic/ Low Energy 対策用
        int offset = 4;

        // キーワードのポジション取得
        //int posEther = payload.indexOf("?mE=");
        //int posWireless = payload.indexOf("&mW=");
        int posBluetooth;
        if (payload.indexOf("&mB=") > 0){
            posBluetooth = payload.indexOf("&mB=");
        } else {
            posBluetooth = payload.indexOf("&mBL=");
            offset = 5;
        }
        int posSKU = payload.indexOf("&c=");
        int posSerial = payload.indexOf("&s=");
        int posVer = payload.indexOf("&v=");

        // PrtInfo Mapにデータ格納
        //prtInfo.put("Ether",payload.substring(posEther+4, posWireless));
        //prtInfo.put("Wireless",payload.substring(posWireless+4, posBluetooth));
        prtInfo.put("Bluetooth",payload.substring(posBluetooth+offset, posSKU));
        prtInfo.put("SKU",payload.substring(posSKU+3, posSerial));
        prtInfo.put("Serial",payload.substring(posSerial+3, posVer));

        return prtInfo;
    }

    // NFC
    // 特定ページの抽出
    public String readTagPage(Tag tag, int page) {

        MifareUltralight mifare = MifareUltralight.get(tag);
        try {
            mifare.connect();
            byte[] payload = mifare.readPages(page);
            String payloadASCII = new String(payload, Charset.forName("US-ASCII"));

            // テスト用
            // Log.v(sLogtag, "Page" + page + ": " + Arrays.toString(payload));
            // Log.v(sLogtag, "Page" + page + ": " + payloadASCII);

            return payloadASCII;
        } catch (IOException e) {
            Log.e(sLogtag, "IOException while reading MifareUltralight message...", e);
        } finally {
            if (mifare != null) {
                try {
                    mifare.close();
                } catch (IOException e) {
                    Log.e(sLogtag, "Error closing tag...", e);
                }
            }
        }
        return null;
    }


    // NFC
    // 特定範囲のページを抽出 v2
    public String readTagPageRange(Tag tag, int startPage, int endPage) {
        MifareUltralight mifare = MifareUltralight.get(tag);
        String payload = "";
        if (endPage%2 > 0 ){
            endPage += 1;
        }

        // 4ページ毎にデータを抽出
        for (int i = startPage; i <= endPage; i=i+4) {
            // Log.v(sLogtag, "Count: " + i);
            payload += readTagPage(tag, i);
        }
        return payload;
    }


    // NFCタグのメタデータ抽出
    private void getTAGinformation(Intent intent) {

        // インテントアクションを取得
        String action = intent.getAction();
        Log.v(sLogtag, "Intent.GetAction: " + action);

        // タグで利用可能なTagTechnology を取得
        // https://developer.android.com/develop/connectivity/nfc/advanced-nfc?hl=ja#tag-tech
        Log.v(sLogtag, "sLogtag.getTechList.Length: " + tagFromIntent.getTechList().length);
        for (String i : tagFromIntent.getTechList()) {
            Log.v(sLogtag, "sLogtag.getTechList: " + i);
        }
    }    
    
    
    /*
    ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    印刷処理関連
    ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
     */

    // 印刷機能： 単体処理
    // 印刷用：Bluetooth 接続のステータス取得
    public Boolean prtGetPrinterConnIsConnected() {
        Boolean b = false;
        try{

            b = thePrinterConn.isConnected();

            if (b == true) {
                Log.v("ZZZ", "Bluetooth connection is open.");
            }
            else if (b == false) {
                Log.v("ZZZ", "Bluetooth connection is closed.");
            }
            else {
                Log.v("ZZZ", "Bluetooth connection is in unexpected status");
            }
        }catch(Exception e){
            e.printStackTrace();
            Log.v("ZZZ", e.toString());
        }
        return b;
    }


    // 印刷機能： 単体処理
    // 印刷用：Bluetooth 接続 OPEN
    public void prtBtOpen(final String btMacAddress) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    //Connection thePrinterConn = new BluetoothConnectionInsecure(btMacAddress);
                    thePrinterConn = new BluetoothConnectionInsecure(btMacAddress);

                    // Initialize
                    Looper.prepare();

                    // Open the connection - physical connection is established here.
                    thePrinterConn.open();

                    Looper.myLooper().quit();
                } catch (Exception e) {
                    // Handle communications error here.
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 印刷機能： 単体処理
    // 印刷用：Bluetooth 接続 Close
    public void prtBtClose() {

        new Thread(new Runnable() {
            public void run() {
                try {

                    // Initialize
                    Looper.prepare();

                    // Make sure the data got to the printer before closing the connection
                    Thread.sleep(500);

                    // Close the insecure connection to release resources.
                    thePrinterConn.close();

                    Looper.myLooper().quit();
                } catch (Exception e) {
                    // Handle communications error here.
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 印刷機能： 単体処理
    // 印刷用：Bluetooth経由でZPLを送信
    public void prtBtSendZpl(String zplData) {

        new Thread(new Runnable() {
            public void run() {
                try {

                    // Initialize
                    Looper.prepare();

                    // This example prints "This is a ZPL test." near the top of the label.
                    //String zplData = "^XA^FO20,20^A0N,25,25^FDThis is a ZPL test.^FS^XZ";

                    // Send the data to printer as a byte array.
                    thePrinterConn.write(zplData.getBytes());


                    Looper.myLooper().quit();
                } catch (Exception e) {
                    // Handle communications error here.
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    // 印刷処理
    // 選択したモードで印刷
    public void SelectModeAndPrint(String btMacAddress, String zplData){
        if (btMacAddress.length() == 12){
            // モードに応じた処理を実行
            if (bFastPrintMode){
                sendZplOverBluetoothContinuousPrint(btMacAddress, zplData);
            }else {
                sendZplOverBluetooth(btMacAddress, zplData);
            }
        } else {
            addTvLogs(getResources().getString(R.string.msgNoBtMACaddress), true);
        }
    }


    // 印刷処理
    // 一気通貫処理： open > send zpl > close
    // 単枚印刷に最適
    // Close処理は別途必要
    public void sendZplOverBluetooth(final String btMacAddress, String zplData) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    //Connection thePrinterConn = new BluetoothConnectionInsecure(btMacAddress);

                    // Instantiate insecure connection for given Bluetooth&reg; MAC Address.
                    thePrinterConn = new BluetoothConnectionInsecure(btMacAddress);

                    // Initialize
                    Looper.prepare();

                    // Open the connection - physical connection is established here.
                    thePrinterConn.open();

                    // This example prints "This is a ZPL test." near the top of the label.
                    //String zplData = "^XA^FO20,20^A0N,25,25^FDThis is a ZPL test.^FS^XZ";

                    // Send the data to printer as a byte array.
                    thePrinterConn.write(zplData.getBytes());

                    // Make sure the data got to the printer before closing the connection
                    Thread.sleep(500);

                    // Close the insecure connection to release resources.
                    thePrinterConn.close();

                    Looper.myLooper().quit();
                } catch (Exception e) {
                    // Handle communications error here.
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    // 印刷処理
    // 一気通貫処理： open(判定) > send zpl
    // 連続・高速印刷に最適
    // Closeが必要な時は別途処理を実装する必要がある
    public void sendZplOverBluetoothContinuousPrint(final String btMacAddress, String zplData) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    //Connection thePrinterConn = new BluetoothConnectionInsecure(btMacAddress);

                    // Instantiate insecure connection for given Bluetooth&reg; MAC Address.
                    //thePrinterConn = new BluetoothConnectionInsecure(btMacAddress);

                    // Initialize
                    Looper.prepare();

                    // Bluetooth 接続が切れている場合は接続をOpen
                    if (!thePrinterConn.isConnected()){
                        // Open the connection - physical connection is established here.
                        thePrinterConn = new BluetoothConnectionInsecure(btMacAddress);
                        thePrinterConn.open();
                        Log.v("ZZZ", "Printer Connection is not opened so PrinterConn.open is executed.");
                    }


                    // This example prints "This is a ZPL test." near the top of the label.
                    //String zplData = "^XA^FO20,20^A0N,25,25^FDThis is a ZPL test.^FS^XZ";

                    // Send the data to printer as a byte array.
                    thePrinterConn.write(zplData.getBytes());

                    // Make sure the data got to the printer before closing the connection
                    //Thread.sleep(500);

                    // Close the insecure connection to release resources.
                    //thePrinterConn.close();

                    Looper.myLooper().quit();
                } catch (Exception e) {
                    // Handle communications error here.
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 印刷処理
    // 印刷用ZPLの生成処理
    public String createZplData(String prtQuantity,
                                String jan,
                                String listPrice,
                                String discountRate,
                                int taxRate){
        /*
        Sample zpl data
        <?xml version="1.0" standalone="no"?>
        <labels _FORMAT="E:demo01.ZPL" _QUANTITY="1"★　print amount
         _PRINTERNAME="Demo Printer" _JOBNAME="demo01">
            <label>
                <variable name="BarcodeDataFull">49018220340443100209</variable> //生成
                <variable name="BarcodeData01">49018220340443</variable> ★ jan
                <variable name="BarcodeData02">1</variable>  // おそらくアイテム数なので、固定値(1)とする
                <variable name="BarcodeData03">0020</variable> //生成
                <variable name="BarcodeData04">9</variable> //CheckDigit デモなので固定値(0)とする
                <variable name="PriceOrg">199</variable> ★ listprice
                <variable name="PriceNew">159</variable> //生成
                <variable name="PriceNewWTax">171</variable> //生成
                <variable name="DiscountRate">20</variable> ★
            </label>
        </labels>
        */

        final String zplDataAA = "<?xml version=\"1.0\" standalone=\"no\"?><labels _FORMAT=\"E:demo01.ZPL\" _QUANTITY=\"";
        final String zplData00 = "\" _PRINTERNAME=\"Demo Printer\" _JOBNAME=\"demo01\"><label><variable name=\"BarcodeDataFull\">";
        final String zplData01 = "</variable><variable name=\"BarcodeData01\">";
        final String zplData02 = "</variable><variable name=\"BarcodeData02\">";
        final String zplData03 = "</variable><variable name=\"BarcodeData03\">";
        final String zplData04 = "</variable><variable name=\"BarcodeData04\">";
        final String zplData05 = "</variable><variable name=\"PriceOrg\">";
        final String zplData06 = "</variable><variable name=\"PriceNew\">";
        final String zplData07 = "</variable><variable name=\"PriceNewWTax\">";
        final String zplData08 = "</variable><variable name=\"DiscountRate\">";
        final String zplDataZZ = "</variable></label></labels>";

        final String sPrtQuantity = prtQuantity;
        final String sBarcodeData01 = jan;
        final String sBarcodeData02 = "1";
        final String sBarcodeData03 = String.format("%05d", Integer.parseInt(discountRate));
        final String sBarcodeData04 = sBarcodeCheckDigit;
        final String sPriceOrg = listPrice;
        final double dPriceNew = Double.valueOf(listPrice) * ((100 - Double.valueOf(discountRate))/100);
        final String sPriceNew = String.valueOf((int)dPriceNew);
        final double dPriceNewWTax = dPriceNew * (100+taxRate) / 100;
        final String sPriceNewWTax = String.valueOf((int)dPriceNewWTax);
        final String sDiscountRate = discountRate;
        final String sBarcodeDataFull = sBarcodeData01 + sBarcodeData02 + sBarcodeData03 + sBarcodeData04;

//        logger("discount rate is " + discountRate);
//        logger(String.format("%04d", Integer.parseInt(discountRate)));


        final String zplData = sZplFormat
                + zplDataAA + prtQuantity
                + zplData00 + sBarcodeDataFull
                + zplData01 + sBarcodeData01
                + zplData02 + sBarcodeData02
                + zplData03 + sBarcodeData03
                + zplData04 + sBarcodeData04
                + zplData05 + sPriceOrg
                + zplData06 + sPriceNew
                + zplData07 + sPriceNewWTax
                + zplData08 + sDiscountRate
                + zplDataZZ;

        logger(zplData);

        return zplData;

    }

    // 印刷処理
    // ZPLを生成して選択されたモードで印刷
    public void createZplAndSelectPrintMode(){

        EditText edItemCode =  findViewById(R.id.edItemCode);
        EditText edItemPrice =  findViewById(R.id.edItemPrice);
        EditText edPrtAmount =  findViewById(R.id.edPrtAmount);
        EditText edDiscountRate =  findViewById(R.id.edDiscountRate);


        // ## For Test
//        String str = edDiscountRate.getText().toString();
//        logger("edDiscountRate.getText().toString() is " + str);
//        String str1 = String.format("%04d", Integer.parseInt(str));
//        logger("String.format is " + str);


        // 商品情報がブランク時の対応
        if (edItemPrice.getText().toString().length() != 0) {
            // ZPL(xml)の生成
            final String sZplData = createZplData(
                    edPrtAmount.getText().toString(),
                    edItemCode.getText().toString(),
                    edItemPrice.getText().toString(),
                    edDiscountRate.getText().toString(),
                    iTaxRate
            );
            // テスト用： 生成ZPLの確認
            // logger(sZplData);

            // 印刷データの送信 -- Bluetooth --> Printer
            SelectModeAndPrint(sPrtBtMac, sZplData);
        } else {
            addTvLogs(getResources().getString(R.string.msgNoItemCode), true);
        }
    }


}











