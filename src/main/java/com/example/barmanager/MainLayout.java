package com.example.barmanager;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route(value = "")
@PageTitle("Main")
public class MainLayout extends AppLayout
{
    public MainLayout()
    {
        DrawerToggle drawerToggle = new DrawerToggle();

        H1 title = new H1("Demo App");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        Tabs tabs = getTabs();


        addToDrawer(tabs);
        addToNavbar(drawerToggle, title);



    }

    private Tabs getTabs()
    {
        Tabs tabs = new Tabs();
        tabs.add(createTab(VaadinIcon.HOME, "Home",MainLayout.class ));

        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        return tabs;
    }

    private Tab createTab(VaadinIcon viewIcon,
                          String tabName, Class target)
    {
        Icon icon = viewIcon.create();
        icon.getStyle().set("box-sizing", "border-box")
                .set("margin-inline-end", "var(--lumo-space-m)")
                .set("margin-inline-start", "var(--lumo-space-xs)")
                .set("padding", "var(--lumo-space-xs)");
        RouterLink link = new RouterLink();
        link.add(icon,new Span(tabName));
        link.setRoute(target);
        link.setTabIndex(-1);
        return new Tab(link);
    }
}
