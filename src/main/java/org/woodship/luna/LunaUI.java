package org.woodship.luna;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.woodship.luna.core.security.Resource;
import org.woodship.luna.core.security.ResourceType;
import org.woodship.luna.core.security.Resource_;
import org.woodship.luna.core.security.Role;
import org.woodship.luna.core.security.Role_;
import org.woodship.luna.core.security.User;
import org.woodship.luna.core.security.UserChangePWEditor;
import org.woodship.luna.core.security.UserService;
import org.woodship.luna.core.security.User_;

import ru.xpoft.vaadin.DiscoveryNavigator;
import ru.xpoft.vaadin.VaadinMessageSource;

import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.addon.jpacontainer.util.DefaultQueryModifierDelegate;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("dashboard")
@org.springframework.stereotype.Component
@Scope("request")
@PreserveOnRefresh//支持F5刷新
public class LunaUI extends UI {
	
	private static final long serialVersionUID = 1L;

	CssLayout root = new CssLayout();

	VerticalLayout loginLayout;

	CssLayout menu = new CssLayout();
	CssLayout content = new CssLayout();

	private DiscoveryNavigator nav;

	private HelpManager helpManager;

    @Autowired
    private VaadinMessageSource messageSource;

	@Autowired()
	@Qualifier("resourceEntityProvider")
	EntityProvider<Resource> resourceEntityProvider;

	@Autowired()
	UserService us;
	@Override
	protected void init(VaadinRequest request) {
		Page.getCurrent().setTitle(messageSource.getMessage("luna.app.name", "Luna"));
		//TODO setConverterFactory 老崔
		//getSession().setConverterFactory(new MyConverterFactory());
		getSession().setErrorHandler(new LunaErrorHandler());
		
		helpManager = new HelpManager(this);

		setLocale(Locale.CHINESE);

		setContent(root);
		root.addStyleName("root");
		root.setSizeFull();

		// Unfortunate to use an actual widget here, but since CSS generated
		// elements can't be transitioned yet, we must
		Label bg = new Label();
		bg.setSizeUndefined();
		bg.addStyleName("login-bg");
		root.addComponent(bg);

		buildLoginView(false);

	}

	@SuppressWarnings("serial")
	private void buildLoginView(boolean exit) {
		if (exit) {
			root.removeAllComponents();
		}
		helpManager.closeAll();
		//        HelpOverlay w = helpManager
		//                .addOverlay(
		//                        "Welcome to the WoodShip Luna",
		//                        "<p>该程序是一个真实的，可以直接使用的程序, 基于 <a href=\"http://vaadin.com\">Vaadin framework</a>构建.</p>" +
		//                        "<p>用户名:  admin密码:  111 </p>" +
		//                        "<p>源代码地址 <a href=\"https://github.com/woodship/luna\">Luna</a>.</p>",
		//                        "login");
		//        w.center();
		//        addWindow(w);

		addStyleName("login");

		loginLayout = new VerticalLayout();
		loginLayout.setSizeFull();
		loginLayout.addStyleName("login-layout");
		root.addComponent(loginLayout);

		final CssLayout loginPanel = new CssLayout();
		loginPanel.addStyleName("login-panel");

		HorizontalLayout labels = new HorizontalLayout();
		labels.setWidth("100%");
		labels.setMargin(true);
		labels.addStyleName("labels");
		loginPanel.addComponent(labels);

		Label welcome = new Label(messageSource.getMessage("luna.login.left", "Welcome"));
		welcome.setSizeUndefined();
		welcome.addStyleName("h4");
		labels.addComponent(welcome);
		labels.setComponentAlignment(welcome, Alignment.MIDDLE_LEFT);

		Label title = new Label(messageSource.getMessage("luna.login.right", "WoodShip Luna"));
		title.setSizeUndefined();
		title.addStyleName("h2");
		title.addStyleName("light");
		labels.addComponent(title);
		labels.setComponentAlignment(title, Alignment.MIDDLE_RIGHT);

		HorizontalLayout fields = new HorizontalLayout();
		fields.setSpacing(true);
		fields.setMargin(true);
		fields.addStyleName("fields");

		final TextField username = new TextField(messageSource.getMessage("luna.login.username", "Username"));
		username.focus();
		username.setValue(User.SUPER_ADMIN_USERNAME);
		fields.addComponent(username);

		final PasswordField password = new PasswordField(messageSource.getMessage("luna.login.password", "Password"));
		password.setValue(User.DEFAULT_PASSWORD);
		fields.addComponent(password);


		final Button signin = new Button("Sign In");
		signin.addStyleName("default");
		fields.addComponent(signin);
		fields.setComponentAlignment(signin, Alignment.BOTTOM_LEFT);

		final ShortcutListener enter = new ShortcutListener("Sign In",KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				signin.click();
			}
		};

		signin.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				String usernameIn = username.getValue();
				String passwordIn = password.getValue();

				try {
					UsernamePasswordToken token = new UsernamePasswordToken(usernameIn, passwordIn);
					// Remember Me built-in, just do this:
						token.setRememberMe(true);

						// With most of Shiro, you'll always want to make sure you're working
						// with the currently executing user,
						// referred to as the subject
						Subject currentUser = SecurityUtils.getSubject();
						// Authenticate
						currentUser.login(token);
						// Store the current user in the service session
						getSession().setAttribute("user", usernameIn);
						// Navigate to main view
						signin.removeShortcutListener(enter);
						buildMainView();
				} catch (UnknownAccountException uae) {
					Notification.show("未知帐户错误");
				} catch (IncorrectCredentialsException ice) {
					Notification.show("登陆密码错误");
				} catch (LockedAccountException lae) {
					Notification.show("帐户已被锁定");
				} catch (ExcessiveAttemptsException eae) {
					Notification.show("登陆次数超过最大次数");
				} catch (AuthenticationException ae) {
					Notification.show("用户名或密码错误");
				} catch (Exception ex) {
					ex.printStackTrace();
					Notification.show(
							"Exception " + ex.getMessage());
				}finally{
				}
			}
		});

		signin.addShortcutListener(enter);

		loginPanel.addComponent(fields);

		loginLayout.addComponent(loginPanel);
		loginLayout.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
	}

	@SuppressWarnings("serial")
	private void buildMainView() {
		final Subject subject =  SecurityUtils.getSubject();
		final User currUser = us.findByUsername(subject.getPrincipal().toString());
		
		//按权限进行过虑
		resourceEntityProvider.setQueryModifierDelegate(
				new DefaultQueryModifierDelegate () {
					@SuppressWarnings("unchecked")
					@Override
					public void filtersWillBeAdded(
							CriteriaBuilder criteriaBuilder,
							CriteriaQuery<?> query,
							List<Predicate> predicates) {
						//管理员不加过虑条件
						if(subject.hasRole(Role.SUPER_ADMIN_ROLE_NAME)){
							return;
						}
						//非管理员按角色过虑菜单
						query.distinct(true);//过虑重复记录
						Root<Resource> root = (Root<Resource>) query.getRoots().iterator().next();
						SetJoin<Resource,Role> rjoin = root.join(Resource_.roles);
						SetJoin<Role,User> ujoin = rjoin.join(Role_.users);
						Predicate p = criteriaBuilder.equal(ujoin.get(User_.username), SecurityUtils.getSubject().getPrincipal());
						predicates.add(p);
					}
				}
		);
		
		//配制菜单japcontainer
		JPAContainer<Resource> con = new JPAContainer<Resource>(Resource.class){
			{
				setEntityProvider(resourceEntityProvider);
				setParentProperty("parent");
			}
			@Override
			public boolean areChildrenAllowed(Object itemId) {
				return super.areChildrenAllowed(itemId)
						&& getItem(itemId).getEntity().getResType().equals(ResourceType.MODULE);
			}
		};

		
		//添加各视图到nav中
		nav = new DiscoveryNavigator(this, content);
		final Map<String,Object> viewsId = new HashMap<String,Object> ();//应用ID,用于选择
		for(Object id : con.getItemIds()){
			Resource res = con.getItem(id).getEntity();
			if(ResourceType.APPLICATION.equals(res.getResType())){
				nav.addBeanView(res.getPath(), res.getViewClass(), true);
				viewsId.put(res.getPath(), res.getId());
			}
		}

		helpManager.closeAll();
		removeStyleName("login");
		root.removeComponent(loginLayout);


		//开始构建视图
		root.addComponent(new HorizontalLayout() {
			{
				setSizeFull();
				addStyleName("main-view");

				addComponent(new VerticalLayout() {
					//左侧菜单视图
					{
						addStyleName("sidebar");
						setWidth("150px");
						setHeight("100%");

						// 顶部
						addComponent(new CssLayout() {
							{
								addStyleName("branding");
								Label logo = new Label(
										"<span>"+messageSource.getMessage("luna.company.name", "WoodShip")
										+"</span><br>"+messageSource.getMessage("luna.app.name", "Luna"),
										ContentMode.HTML);
								logo.setSizeUndefined();
								addComponent(logo);
								// addComponent(new Image(null, new
								// ThemeResource(
								// "img/branding.png")));
							}
						});

						// 中间主菜单区域
						addComponent(menu);
						setExpandRatio(menu, 1);

						// 底部用户设置区域
						addComponent(new VerticalLayout() {
							{
								setSizeUndefined();
								addStyleName("user");
								Image profilePic = new Image(null,new ThemeResource("img/profile-pic.png"));
								profilePic.setWidth("34px");
								addComponent(profilePic);
								Label userName = new Label(currUser.getShowName());
								userName.setSizeUndefined();
								addComponent(userName);

								Command cmd = new Command() {
									@Override
									public void menuSelected(MenuItem selectedItem) {
										//TODO 验证原密码
										UserChangePWEditor w = new UserChangePWEditor(currUser, us);
										w.center();
										UI.getCurrent().addWindow(w);
									}
								};
								MenuBar settings = new MenuBar();
								MenuItem settingsMenu = settings.addItem("",
										null);
								settingsMenu.setStyleName("icon-cog");
								settingsMenu.addItem("修改密码", cmd);
//								settingsMenu.addItem("Preferences", cmd);
								settingsMenu.addSeparator();
								settingsMenu.addItem("设置", new Command() {
									@Override
									public void menuSelected(MenuItem selectedItem) {
										Notification.show("暂未实现");
									}
								});
								addComponent(settings);

								Button exit = new NativeButton("Exit");
								exit.addStyleName("icon-cancel");
								exit.setDescription("Sign Out");
								addComponent(exit);
								exit.addClickListener(new ClickListener() {
									@Override
									public void buttonClick(ClickEvent event) {
										buildLoginView(true);
//										SecurityUtils.getSubject().logout();
									}
								});
							}
						});
					}
				});
				// 右侧内容区
				addComponent(content);
				content.setSizeFull();
				content.addStyleName("view-content");
				setExpandRatio(content, 1);
			}

		});

		menu.removeAllComponents();
		final Tree tree = new Tree();
		tree.setMultiSelect(false);
		tree.setContainerDataSource(con);
		tree.setItemCaptionPropertyId("name");
		tree.setImmediate(true);//解决菜单与视图不同步问题
		menu.addComponent(tree);
		//默认展开树菜单
		for(Object id : tree.getItemIds()){
			tree.expandItem(id);
		}

		menu.addStyleName("menu");
		menu.setHeight("100%");


		String f = Page.getCurrent().getUriFragment();
		if (f != null && f.startsWith("!")) {
			f = f.substring(1);
		}else{
			f = "";
		}

		//进入地址指定视图
		nav.navigateTo(f);
		tree.select(viewsId.get(f));

		//增加点击事件
		tree.addItemClickListener(new ItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void itemClick(ItemClickEvent event) {
				JPAContainerItem<Resource> item = (JPAContainerItem<Resource>) event.getItem();
				Resource res =  item.getEntity();
				//不是当前视图时，切换视图
				if ( ResourceType.APPLICATION.equals(res.getResType()) 
						& !nav.getState().equals( res.getPath())){
					nav.navigateTo(res.getPath());
				}
			}
		});

	}


	HelpManager getHelpManager() {
		return helpManager;
	}

}
