package com.example.mark.ireportmaininterface;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class CreateReport extends Activity {

    Button btnNextAction;
    Button btnPrevAction;
    TextView tv;
    final Context context = this;
    RadioButton btn;
    public String[] questions;
    String[][] answers;
    String[] storedAnswers;
    boolean[] storedAnswersFlags;
    public int currNum = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);
        //Create an array of radio buttons based on the number of choices on the question made
        try {
            //generateRadioArray();
            //Questions
            questions = new String[2]; //get number of questions
            questions[0] = "What issues are present?";
            questions[1] = "Did/Does someone get hurt?";

            //Answers
            answers = new String[questions.length][];//number of answers per question
            //question 1
            answers[0] = new String[4];
            answers[0][0] = "Infrastructure";
            answers[0][1] = "Sanitation";
            answers[0][2] = "Traffic";
            answers[0][3] = "Crime";
            //question 2
            answers[1] = new String[2];
            answers[1][0] = "Yes";
            answers[1][1] = "No";
            //stored answers
            storedAnswers = new String[questions.length];//allocate memory for selected answers
            storedAnswersFlags = new boolean[questions.length];

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        tv = (TextView)findViewById(R.id.mytextview);
        btnNextAction = (Button)findViewById(R.id.btnNext);
        btnNextAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (currNum >= questions.length-1)
                    {
                        btnNextAction.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        currNum++;
                        tv.setText(questions[currNum]);
                        initializeRadioChoices();
                        btnPrevAction.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    tv.setText("Not in index");
                    LinearLayout li = (LinearLayout)findViewById(R.id.radiolayout);
                    li.removeAllViews();
                }
            }
        });
        btnPrevAction = (Button)findViewById(R.id.btnPrevious);
        btnPrevAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (currNum <= 0)
                    {
                        btnPrevAction.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        currNum--;
                        tv.setText(questions[currNum]);
                        initializeRadioChoices();
                        btnNextAction.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    tv.setText("Not in index");
                    LinearLayout li = (LinearLayout)findViewById(R.id.radiolayout);
                    li.removeAllViews();
                }
            }
        });
    }
    RadioButton rdSelectBtn;
    private void initializeRadioChoices()
    {
        LinearLayout li = (LinearLayout)findViewById(R.id.radiolayout);
        li.removeAllViews();
        for (int i = 0; i < answers[currNum].length; i++)
        {
            final RadioButton myRadio = new RadioButton(this);
            myRadio.setId(i);
            myRadio.setText(answers[currNum][i]);
            li = (LinearLayout)findViewById(R.id.radiolayout);
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            li.addView(myRadio, lp);
            rdSelectBtn = (RadioButton)findViewById(myRadio.getId());
            rdSelectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(view.getContext(), myRadio.getText(), Toast.LENGTH_SHORT).show();
                    storedAnswers[currNum] = myRadio.getText().toString();
                    for (int i = 0; i < answers[currNum].length; i++)
                    {
                        if (rdSelectBtn.getId() == i)
                            storedAnswersFlags[rdSelectBtn.getId()] = true;
                        else
                            storedAnswersFlags[i] = false;
                    }
                    Toast.makeText(view.getContext(), storedAnswers[currNum].toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private String getQuestions(int qNum)
    {
        return questions[qNum];
    }
    private String getAnswer(int aNum, int aAns)
    {
        return answers[aNum][aAns];
    }
    private void generateRadioArray()
    {
        for (int i = 0; i < 5; i++)
        {
            RadioButton myRadio = new RadioButton(this);//use array
            myRadio.setId(i);//set id
            final int id_ = myRadio.getId();
            myRadio.setText("button " + id_);//assign a name
            LinearLayout li = (LinearLayout)findViewById(R.id.radiolayout);
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            li.addView(myRadio, lp);
            btn = (RadioButton) findViewById(id_);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(view.getContext(), "Button clicked index = " + id_, Toast.LENGTH_SHORT).show();
                    tv.setText(String.valueOf(id_));
                }
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_report, menu);
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
}
