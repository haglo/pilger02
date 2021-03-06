package org.app.view.account;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.app.controler.AccountService;
import org.app.helper.I18n;
import org.app.model.entity.Account;

import com.vaadin.cdi.CDIView;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@CDIView(I18n.ACCOUNT_VIEW)
public class AccountView extends HorizontalLayout implements View {

	@Inject
	AccountService accountService;
	
	private I18n i18n;
	private Account account;
	private Account selectedAccount;
	private Set<Account> selectedAccounts;
	private TextField firstEntryField = new TextField();
	private TextField txfPassword = new TextField();
	private Grid<Account> grid;

	public AccountView() {
		i18n = new I18n();
		setMargin(new MarginInfo(false, true, true, true));
	}

	@PostConstruct
	void init() {
		setSizeFull();
		setWidth(I18n.WINDOW_WIDTH);

		VerticalLayout content = new VerticalLayout();
		selectedAccounts = new HashSet<>();
		List<Account> accountList = accountService.findAll();

		DataProvider<Account, ?> dataProvider = DataProvider.ofCollection(accountList);
		grid = new Grid<Account>();
		grid.setSizeFull();
		grid.setSelectionMode(SelectionMode.MULTI);
		grid.addSelectionListener(event -> {
			selectedAccounts = event.getAllSelectedItems();
		});

		grid.getEditor().setEnabled(true);
		grid.getEditor().addSaveListener(event -> {
			account = event.getBean();
			updateRow(account);
		});

		grid.setDataProvider(dataProvider);
		grid.addColumn(Account::getUsername).setCaption(i18n.ACCOUNT_USERNAME)
				.setEditorComponent(firstEntryField, Account::setUsername).setId(i18n.ACCOUNT_USERNAME);
		grid.addColumn(Account::getPassword).setCaption(i18n.ACCOUNT_PASSWORD).setEditorComponent(txfPassword,
				Account::setPassword);

		Button add = new Button("+");
		add.addClickListener(event -> {
			getUI().addWindow(new AccountNewView(this));
		});

		Button delete = new Button("-");
		delete.addClickListener(event -> deleteRow());

		Button detail = new Button("", ev -> {
			if (onlyOneSelected(selectedAccounts)) {
				for (Account entry : selectedAccounts) {
					selectedAccount = entry;
					getUI().addWindow(new AccountDetailView(this, selectedAccount));
				}
			}
		});
		detail.setIcon(VaadinIcons.PENCIL);

		CssLayout accountNavBar = new CssLayout(add, delete, detail);
		accountNavBar.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		content.addComponent(grid);
		content.addComponent(accountNavBar);
		addComponent(content);
		setDefaultComponentAlignment(Alignment.TOP_CENTER);
	}

	private void deleteRow() {
		if (selectedAccounts.size() == 0) {
			return;
		}
		for (Account entry : selectedAccounts) {
			accountService.remove(entry.getId());
		}
		refreshGrid();
	}

	public void updateRow(Account account) {
		accountService.update(account);
		refreshGrid();
	}

	public void refreshGrid() {
		List<Account> list = accountService.findAll();
		grid.sort(i18n.ACCOUNT_USERNAME, SortDirection.ASCENDING);
		grid.setItems(list);
	}

	public AccountService getAccountService() {
		return accountService;
	}

	private boolean onlyOneSelected(Set<Account> selected) {
		boolean isCorrect = true;
		if (selected.size() > 1) {
			Notification.show(i18n.NOTIFICATION_ONLY_ONE_ITEM);
			isCorrect = false;
		}
		if (selected.size() < 1) {
			Notification.show(i18n.NOTIFICATION_EXACT_ONE_ITEM);
			isCorrect = false;
		}
		return isCorrect;

}
}
