/**
 * Copyright 2009-2013 Oy Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.woodship.luna.core.security;

import org.springframework.util.StringUtils;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class UserChangePWEditor extends Window  {
	private PasswordField pwa = new PasswordField("新密码");
	private PasswordField pwb = new PasswordField("确认");
	public UserChangePWEditor(final User user,  final UserService us ,final JPAContainer<User> mainContainer) {
		this.setCaption("重置密码");
		final FormLayout formLayout = new FormLayout();
		formLayout.setMargin(true);

		//增加字段
		formLayout.addComponent(pwa);
		formLayout.addComponent(pwb);

		final Label error = new Label("", ContentMode.HTML);
		error.setVisible(false);
		formLayout.addComponent(error);


		//处理保存事件
		Button saveButton = new Button("保存");
		saveButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				//编辑的直接提交即可
				if(StringUtils.isEmpty(pwa.getValue())){
					error.setValue("<div style='color:red'>密码不能为空</div>");
					error.setVisible(true);
				}else if(!pwa.getValue().equals(pwb.getValue())){
					error.setValue("<div style='color:red'>两次输入不一致</div>");
					error.setVisible(true);
				}else{
					us.changePassword(user);
					mainContainer.refreshItem(user.getId());
					error.setVisible(false);
					Notification.show("密码修改成功");
				}
			}
		});


		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setMargin(true);
		buttons.addComponent(saveButton);
		formLayout.addComponent(buttons);
		formLayout.setComponentAlignment(buttons, Alignment.MIDDLE_LEFT);
		setContent(formLayout);
	}

}
