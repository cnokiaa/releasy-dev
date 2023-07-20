package com.releasy.android.bean;

public class FaqBean {

	private String question;
	private String answer;
	
	public void setQuestion(String question){
		this.question = question;
	}
	public String getQuestion(){
		return question;
	}
	
	public void setAnswer(String answer){
		this.answer = answer;
	}
	public String getAnswer(){
		return answer;
	}
	
	public FaqBean(){}
	
	public FaqBean(String question, String answer){
		this.question = question;
		this.answer = answer;
	}
}
