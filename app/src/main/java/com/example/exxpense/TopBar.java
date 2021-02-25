package com.example.exxpense;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import model.Group;
import model.GroupManager;
import model.User;


public class TopBar extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private GroupManager groupManager;
    private Boolean menuVisible;


    public TopBar() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static TopBar newInstance(GroupManager groupManager, Boolean menuVisible) {
        TopBar fragment = new TopBar();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, groupManager);
        args.putBoolean(ARG_PARAM2, menuVisible);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupManager = (GroupManager) getArguments().getSerializable(ARG_PARAM1);
            menuVisible = getArguments().getBoolean(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_top_bar, container, false);
        if (groupManager != null && menuVisible != null) {
            TextView title = root.findViewById(R.id.title);
            TextView code = root.findViewById(R.id.code);
            ImageView menuIcon = root.findViewById(R.id.menu_icon);

            groupManager.addGroup("DCX","wakacje", new User("Ala"));
            groupManager.addGroup("ABC","kolacje", new User("Ala"));

            title.setText(groupManager.getCurrentGroup().getName());
            code.setText(groupManager.getCurrentGroup().getCode());
            if (menuVisible) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), menuIcon);
                popupMenu.getMenu().add(R.string.join_new_group);
                popupMenu.getMenu().add(R.string.create_new_group);
                for (Group group : groupManager.getGroups()) {
                    popupMenu.getMenu().add(group.getName());
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        CharSequence itemTitle = item.getTitle();
                        if (itemTitle.equals(getString(R.string.join_new_group))) {
                            Intent intent = new Intent(getActivity(),JoinGroup.class);
                            requireActivity().startActivity(intent);
                        } else if (itemTitle.equals(getString(R.string.create_new_group))) {
                            Intent intent = new Intent(getActivity(),CreateGroup.class);
                            requireActivity().startActivity(intent);
                        } else {
                            for (Group group : groupManager.getGroups()) {
                                if (itemTitle.equals(group.getName())) {
                                    groupManager.setCurrentGroup(group);
                                    title.setText(groupManager.getCurrentGroup().getName());
                                    code.setText(groupManager.getCurrentGroup().getCode());
                                }
                            }
                        }
                        return false;
                    }
                });
                menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupMenu.show();
                    }
                });
            }else {
                menuIcon.setVisibility(View.INVISIBLE);
            }
        }
        return root;
    }
}