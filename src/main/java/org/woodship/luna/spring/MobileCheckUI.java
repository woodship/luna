package org.woodship.luna.spring;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@Theme(Reindeer.THEME_NAME)
@Title("Copernicium 技术平台")
@SuppressWarnings("serial")
public class MobileCheckUI extends UI {

    @Override
    protected void init(final VaadinRequest request) {
        setWidth("400px");
        setContent(new VerticalLayout() {
            {
                setMargin(true);
                addComponent(new Label(
                        "<h1>Copernicium 技术平台</h1><h3>Copernicium 技术平台暂时还有提供移动设备上访问!</h3><p>如果你愿意访问桌面版, <a href=\""
                                + request.getContextPath()
                                + request.getPathInfo()
                                + "?mobile=false\">请点击这里</a>.",
                        ContentMode.HTML));
            }
        });

    }
}
