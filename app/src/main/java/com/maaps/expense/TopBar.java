package com.maaps.expense;

import android.app.Activity;
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
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.maaps.expense.helpers.AccountHelper;

import java.util.Map;

import modelv2.ShallowGroup;
import modelv2.UserSession;


public class TopBar extends Fragment {

    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Expense.TopBar";

    private RefreshCurrentGroup refreshCurrentGroup;
    private LogOutInterface logOutInterface;

    private UserSession userSession;
    private Boolean menuVisible;


    public TopBar() {
        // Required empty public constructor
    }

    public void setRefreshCurrentGroup(RefreshCurrentGroup refreshCurrentGroup) {
        this.refreshCurrentGroup = refreshCurrentGroup;
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

                menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(getActivity(), menuIcon);
                        popupMenu.getMenu().add(R.string.join_new_group);
                        popupMenu.getMenu().add(R.string.create_new_group);
                        popupMenu.getMenu().add(getString(R.string.share_group));
                        for (ShallowGroup group : userSession.getGroups()) {
                            if (!group.getGroupId().equals(userSession.getCurrentShallowGroup().getGroupId())) {
                                popupMenu.getMenu().add(group.getGroupName());
                            }
                        }
                        popupMenu.getMenu().add(R.string.logout);

                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                CharSequence itemTitle = item.getTitle();
                                if (itemTitle.equals(getString(R.string.join_new_group))) {
                                    Intent intent = new Intent(getActivity(), JoinGroup.class);
                                    requireActivity().startActivity(intent);
                                } else if (itemTitle.equals(getString(R.string.create_new_group))) {
                                    Intent intent = new Intent(getActivity(), CreateGroup.class);
                                    requireActivity().startActivity(intent);
                                } else if (itemTitle.equals(getString(R.string.share_group))) {
                                    Intent intent = new Intent(requireActivity(), Share.class);
                                    requireActivity().startActivity(intent);
                                } else if (itemTitle.equals(getString(R.string.logout))) {
                                    if (logOutInterface != null) {
                                        logOutInterface.signOut();
                                    }
                                } else {
                                    ShallowGroup tmp = null;
                                    for (ShallowGroup group : userSession.getGroups()) {
                                        if (itemTitle.equals(group.getGroupName())) {
                                            tmp = group;
                                            break;
                                        }
                                    }
                                    if (tmp != null) {
                                        userSession.changeCurrentGroup(tmp);
                                    }
                                }
                                return false;
                            }
                        });


                        popupMenu.show();
                    }
                });
            } else {
                menuIcon.setVisibility(View.INVISIBLE);
            }
        }
        return root;

    }

    public void setLogOutInterface(LogOutInterface logOutInterface) {
        this.logOutInterface = logOutInterface;
    }

    public interface LogOutInterface {
        void signOut();
    }

    public interface RefreshCurrentGroup {
        void refreshCurrentGroup(Map.Entry<String, String> group);
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