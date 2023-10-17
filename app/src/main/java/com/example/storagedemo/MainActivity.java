package com.example.storagedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private EditText textView;
    private static final int CREATE_REQUEST_CODE=40;
    private static final int OPEN_REQUEST_CODE=41;
    private static final int SAVE_REQUEST_CODE=42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView=findViewById(R.id.fileText);
    }

    public void  newFile(View View){
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE,"newfile.txt");
        startActivityForResult(intent, CREATE_REQUEST_CODE);
    }

    public void saveFile(View view){
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        startActivityForResult(intent,SAVE_REQUEST_CODE);
    }

    public  void openFile(View view){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(intent,OPEN_REQUEST_CODE);

    }


    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        super.onActivityResult(requestCode, resultCode, resultData);
        Uri currentUri=null;

        if (resultCode== Activity.RESULT_OK){
            if(requestCode==CREATE_REQUEST_CODE){
                if (resultData !=null){
                    textView.setText("");
                }
            } else if(requestCode==SAVE_REQUEST_CODE){
                if(resultData !=null){
                    currentUri=resultData.getData();
                    writeFileContent(currentUri);
                }
            } else if(requestCode==OPEN_REQUEST_CODE){
                if (resultData != null){
                    currentUri = resultData.getData();
                    try{
                        String content=readFileContent(currentUri);
                        textView.setText(content);
                    } catch (IOException e){
                        //Handle error here
                    }

                }
            }
        }
    }


    private void writeFileContent(Uri uri){
        try{
            ParcelFileDescriptor pfd=this.getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream=new FileOutputStream(pfd.getFileDescriptor());
            String textContent=textView.getText().toString();
            fileOutputStream.write(textContent.getBytes());
            fileOutputStream.close();
            pfd.close();
            textView.setText("");

        }catch(FileNotFoundException e){
                e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private String readFileContent(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String currentLine;
        while ((currentLine = reader.readLine()) != null){
            stringBuilder.append(currentLine).append("\n");
        }
        assert  inputStream != null;
        inputStream.close();
        return stringBuilder.toString();
    }

}