package org.app.view.account;

import java.util.EnumSet;

import org.app.controler.AccountService;
import org.app.helper.I18n;
import org.app.model.entity.Account;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AccountNewView extends Window {

	private static final long serialVersionUID = 1L;

	private I18n i18n;
	private TextField txfUsername;
	private TextField txfMailaddress;
	private TextField txfPassword;
	private TextArea txaComment;
	private Button saveButton;
	private BCryptPasswordEncoder encoder;

	private AccountService accountService;
	private Account newAccount;

	@SuppressWarnings("static-access")
	public AccountNewView(AccountView accountView) {
		encoder = new BCryptPasswordEncoder();
		i18n = new I18n();
		this.accountService = accountView.getAccountService();
		this.newAccount = new Account();
		saveButton = new Button(i18n.BASIC_SAVE);
		saveButton.setEnabled(true);

		this.setCaption(i18n.ACCOUNT_WINDOW_NEW_CAPTION);
		VerticalLayout subContent = new VerticalLayout();
		this.setContent(subContent);
		this.center();

		try {
			txfUsername = new TextField(i18n.ACCOUNT_USERNAME);
			subContent.addComponent(txfUsername);

			txfPassword = new TextField(i18n.ACCOUNT_PASSWORD);
			subContent.addComponent(txfPassword);

			txfMailaddress = new TextField(i18n.BASIC_EMAIL);
			subContent.addComponent(txfMailaddress);

			txaComment = new TextArea(i18n.BASIC_COMMENT);
			subContent.addComponent(txaComment);

			subContent.addComponent(saveButton);

			saveButton.addClickListener(event -> {
				newAccount.setUsername(txfUsername.getValue());
				newAccount.setMailaddress(txfMailaddress.getValue());
				newAccount.setPassword(encode(txfPassword.getValue()));
				newAccount.setComment(txaComment.getValue());
				accountService.create(newAccount);
				accountView.refreshGrid();
				getUI().getCurrent().removeWindow(this);
			});

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String encode(String password) {
		return encoder.encode(password);
	}

}