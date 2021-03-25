package com.maaps.expense;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.util.Map;
import modelv2.ShallowGroup;
import modelv2.UserSession;


public class TopBar extends Fragment {

    private static final String ARG_PARAM2 = "menu_visible";

    private LogOutInterface logOutInterface;
    private UserSession userSession;
    private Boolean menuVisible;


    public TopBar() {
        // Required empty public constructor
    }

    public static TopBar newInstance(Boolean menuVisible) {
        TopBar fragment = new TopBar();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM2, menuVisible);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userSession = UserSession.getInstance();
            menuVisible = getArguments().getBoolean(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_top_bar, container, false);
        if (userSession != null && menuVisible != null) {
            userSession = UserSession.getInstance();
            TextView title = root.findViewById(R.id.title);
            TextView code = root.findViewById(R.id.code);
            ImageView menuIcon = root.findViewById(R.id.menu_icon);


            title.setText(userSession.getCurrentShallowGroup().getGroupName());
            code.setText(userSession.getCurrentShallowGroup().getGroupId());
            if (menuVisible) {
                menuIcon.setOnClickListener(v -> initializeMenuIconOnClickLogic(menuIcon));
            } else {
                menuIcon.setVisibility(View.INVISIBLE);
            }
        }
        return root;

    }

    private void initializeMenuIconOnClickLogic(ImageView menuIcon) {
        PopupMenu programMenu = createPopUpMenu(menuIcon);
        programMenu.setOnMenuItemClickListener(
                TopBar.this::initializePopUpMenuOnClickLogic);
        programMenu.show();
    }

    private PopupMenu createPopUpMenu(ImageView menuIcon) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), menuIcon);
        popupMenu.getMenu().add(R.string.join_new_group);
        popupMenu.getMenu().add(R.string.create_new_group);
        popupMenu.getMenu().add(getString(R.string.share_group));

        addGroupsToPopUpMenu(popupMenu);

        popupMenu.getMenu().add(R.string.logout);

        return popupMenu;
    }

    private void addGroupsToPopUpMenu(PopupMenu popupMenu) {
        for (ShallowGroup group : userSession.getGroups()) {
            if (!group.getGroupId().equals(userSession.getCurrentShallowGroup().getGroupId())) {
                popupMenu.getMenu().add(group.getGroupName());
            }
        }
    }

    private boolean initializePopUpMenuOnClickLogic(MenuItem item) {
        CharSequence clickedItem = item.getTitle();

        if (clickedItem.equals(getString(R.string.join_new_group))) {
            startJoinGroupActivity();

        } else if (clickedItem.equals(getString(R.string.create_new_group))) {
            startCreateGroupActivity();

        } else if (clickedItem.equals(getString(R.string.share_group))) {
            startShareGroupActivity();

        } else if (clickedItem.equals(getString(R.string.logout))) {
            logOut();

        } else {
            changeGroup(clickedItem);

        }
        return false;
    }

    private void logOut() {
        if (logOutInterface != null) {
            logOutInterface.signOut();
        }
    }

    private void changeGroup(CharSequence clickedItem) {
        ShallowGroup clickedGroup = null;
        for (ShallowGroup group : userSession.getGroups()) {
            if (clickedItem.equals(group.getGroupName())) {
                clickedGroup = group;
                break;
            }
        }
        if (clickedGroup != null) {
            userSession.changeCurrentGroup(clickedGroup);
        }
    }

    private void startShareGroupActivity() {
        Intent intent = new Intent(requireActivity(), Share.class);
        requireActivity().startActivity(intent);
    }

    private void startCreateGroupActivity() {
        Intent intent = new Intent(getActivity(), CreateGroup.class);
        requireActivity().startActivity(intent);
    }

    private void startJoinGroupActivity() {
        Intent intent = new Intent(getActivity(), JoinGroup.class);
        requireActivity().startActivity(intent);
    }

    public void setLogOutInterface(LogOutInterface logOutInterface) {
        this.logOutInterface = logOutInterface;
    }

    public interface LogOutInterface {
        void signOut();
    }


    public static int refreshTopBar(int previousTopBar, AppCompatActivity activity,TopBar topBar) {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.replace(previousTopBar, topBar, String.valueOf(topBar.hashCode()));
            fragmentTransaction.commit();

        return topBar.getId();
    }
}