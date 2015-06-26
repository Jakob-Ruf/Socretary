package de.lucasschlemm.socretary.classes;

/**
 * Created by jakob.ruf on 27.05.2015.
 */
public class AutomatedMessage {
	private String text;
	private long id;
	private long amount_sent;

	public AutomatedMessage(){
		this.setAmount_sent(0l);
		this.setText("Lorem ipsum");
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getAmount_sent() {
		return amount_sent;
	}

	public void setAmount_sent(long amount_sent) {
		this.amount_sent = amount_sent;
	}
}
