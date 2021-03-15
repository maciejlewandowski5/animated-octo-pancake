package modelv2;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class UserSession {

    private static final String TAG = "UserSession";
    private static UserSession currentSession = null;
    private User currentUser;
    private ShallowGroup currentShallowGroup;
    private ArrayList<ShallowGroup> groups;
    private boolean groupListenerSet;
    private ListenerRegistration groupListener;
    private boolean expensesListenerSet;
    private ListenerRegistration expenseListener;
    private Group currentGroup;
    private DebtManager debtManager;

    private OnGroupUpdated onGroupUpdated;
    private OnExpensesUpdated onExpensesUpdated;
    private OnExpensePushed onExpensePushed;
    private OnGroupPushed onGroupPushed;
    private OnJoinGroupError onJoinGroupError;
    private OnJoinGroupSuccess onJoinGroupSuccess;
    private OnDeptUpdated onDebtUpdated;
    private OnCurrentGroupNull onCurrentGroupNull;

    FirebaseFirestore db;


    private UserSession() {
        groupListenerSet = false;
        expensesListenerSet = false;
        groups = new ArrayList<>();
        currentGroup = null;
        onGroupUpdated = null;
        onExpensesUpdated = null;
        onGroupPushed = null;
        onCurrentGroupNull = null;
        debtManager = null;
        db = FirebaseFirestore.getInstance();

        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    currentUser = new User(documentSnapshot.getString("name"), documentSnapshot.getId());
                    try {
                        currentShallowGroup = new ShallowGroup((Map<String, Object>) documentSnapshot.getData().get("currentGroup"));
                        if (currentShallowGroup.groupId == null && onCurrentGroupNull != null) {
                            onCurrentGroupNull.onCurrentGroupNull();
                            return;
                        }
                        ((Map<String, Object>) documentSnapshot.getData().get("groups")).forEach((k, v) -> {
                            groups.add(new ShallowGroup(k, (String) v));
                        });
                    } catch (ClassCastException e) {
                        throw e;
                    }
                    setGroupListener();
                }
            }
        });

    }

    public static UserSession getInstance() {
        if (currentSession == null) {
            currentSession = new UserSession();
        }
        return currentSession;
    }

    public static Map<String, Object> CreateNewUser(String name, String newUserId) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        Map<String, Object> nested = new HashMap<>();
        result.put("groups", nested);
        result.put("currentGroup", nested);
        return result;
    }


    public ShallowGroup getCurrentShallowGroup() {
        return currentShallowGroup;
    }

    public ArrayList<ShallowGroup> getGroups() {
        return groups;
    }

    private void setGroupListener() {
        if (!groupListenerSet) {
            db = FirebaseFirestore.getInstance();
            groupListener = db.collection("Groups").document(currentShallowGroup.getGroupId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }


                    if (value != null && value.exists()) {
                        currentGroup = new Group(value);
                        debtManager = new DebtManager(value);
                        debtManager.simplifyDebts();
                        if (onDebtUpdated != null) {
                            onDebtUpdated.onDebtUpdated(debtManager.getExpenses());
                        }
                        if (onGroupUpdated != null) {
                            onGroupUpdated.onGroupUpdated(currentGroup);
                        }
                        setExpenseListener();
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });
            groupListenerSet = true;
        }
    }

    private void setExpenseListener() {
        if (!expensesListenerSet) {
            db = FirebaseFirestore.getInstance();
            expenseListener = db.collection("Groups").document(currentShallowGroup.getGroupId()).collection("Expenses").limit(8).orderBy("dateTime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }
                    ArrayList<Expense> expenses = new ArrayList<>();
                    if (value != null) {
                        currentGroup.clearExpenses();
                        for (DocumentSnapshot ds : value.getDocuments()) {
                            currentGroup.addExpenseQuietly(new Expense(ds));
                        }
                        if (onExpensesUpdated != null) {
                            onExpensesUpdated.onExpensesUpdated(currentGroup.getExpenses());
                        }
                    }
                }
            });
            expensesListenerSet = true;
        }
    }


    public void changeCurrentGroup(ShallowGroup shallowGroup) {
        if (groups.contains(shallowGroup)) {
            this.removeGroupListener();
            this.removeExpenseListener();
            ShallowGroup tmp = currentShallowGroup;
            currentShallowGroup = groups.get(groups.indexOf(shallowGroup));
            groups.remove(currentShallowGroup);
            groups.add(tmp);
            db.collection("Users").document(currentUser.getId()).update(this.toMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    setGroupListener();
                    if (onGroupPushed != null) {
                        onGroupPushed.onGroupPushed();
                    }
                }
            });

        }
    }


    public void createNewGroup(String name, String code) {
        Group group = new Group(name, code, currentUser);
        db.collection("Groups").add(group.toMap()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                ShallowGroup shallowGroup = new ShallowGroup(documentReference.getId(), name);
                groups.add(shallowGroup);
                changeCurrentGroup(shallowGroup);
            }
        });
    }

    public void joinGroup(String groupId) {
        if (!groups.contains(groupId)) {
            db.collection("Groups").document(groupId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        ShallowGroup shallowGroup = new ShallowGroup(documentSnapshot.getId(), documentSnapshot.getString("name"));
                        groups.add(shallowGroup);
                        changeCurrentGroup(shallowGroup);
                        Group group = new Group(documentSnapshot);
                        group.addUser(currentUser);
                        documentSnapshot.getReference().update(group.toMap());
                        if (onJoinGroupSuccess != null) {
                            onJoinGroupSuccess.onJoinGroupSuccess();
                        }
                    } else {
                        if (onJoinGroupError != null) {
                            onJoinGroupError.onJoinGroupError();
                        }
                    }
                }
            });
        }
    }

    public void addExpenses(ArrayList<Expense> expenses, int beginWithIndex) {
        if (expenses.size() == beginWithIndex) {
            return;
        } else {
            currentGroup.addExpense(expenses.get(beginWithIndex));
            db.collection("Groups").document(currentShallowGroup.getGroupId()).update(currentGroup.toMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    db.collection("Groups").document(currentShallowGroup.getGroupId()).collection("Expenses").add(expenses.get(beginWithIndex).toMap()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            if (onExpensePushed != null) {
                                onExpensePushed.onExpensePushed();
                            }
                            addExpenses(expenses, beginWithIndex + 1);
                        }
                    });
                }
            });
        }

    }


    public void addExpense(Expense expense) {
        currentGroup.addExpense(expense);
        db.collection("Groups").document(currentShallowGroup.getGroupId()).update(currentGroup.toMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                db.collection("Groups").document(currentShallowGroup.getGroupId()).collection("Expenses").add(expense.toMap()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        if (onExpensePushed != null) {
                            onExpensePushed.onExpensePushed();
                        }
                    }
                });
            }
        });
    }


    public void addExpense(String name, double amount, Date date, User payer, ArrayList<User> borrowers) {
        Expense expense = new Expense(name, amount, date, payer, borrowers);
        currentGroup.addExpense(expense);

        db.collection("Groups").document(currentShallowGroup.getGroupId()).update(currentGroup.toMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                db.collection("Groups").document(currentShallowGroup.getGroupId()).
                        collection("Expenses").add(expense.toMap()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        if (onExpensePushed != null) {
                            onExpensePushed.onExpensePushed();
                        }
                    }
                });

            }
        });

    }

    public void editExpense(Expense expense) {
        currentGroup.addExpense(expense);
        db.collection("Groups").document(currentShallowGroup.getGroupId()).update(currentGroup.toMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                db.collection("Groups").document(currentShallowGroup.getGroupId()).
                        collection("Expenses").document(expense.getId()).update(expense.toMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (onExpensePushed != null) {
                            onExpensePushed.onExpensePushed();
                        }
                    }
                });
            }
        });
    }

    private void removeGroupListener() {
        if (groupListenerSet) {
            groupListener.remove();
            groupListenerSet = false;
        }
    }

    private void removeExpenseListener() {
        if (expensesListenerSet) {
            expenseListener.remove();
            this.expensesListenerSet = false;
        }
    }

    public void setOnExpensePushed(OnExpensePushed onExpensePushed) {
        this.onExpensePushed = onExpensePushed;
    }

    public void setOnGroupUpdated(OnGroupUpdated onGroupUpdated) {
        this.onGroupUpdated = onGroupUpdated;
    }

    public void setOnGroupPushed(OnGroupPushed onGroupPushed) {
        this.onGroupPushed = onGroupPushed;
    }

    public void removeOnGroupUpdated() {
        this.onGroupUpdated = null;
    }

    public void removeOnGroupPushed() {
        this.onGroupPushed = null;
    }

    public void setOnJoinGroupError(OnJoinGroupError onJoinGroupError) {
        this.onJoinGroupError = onJoinGroupError;
    }

    public void setOnJoinGroupSuccess(OnJoinGroupSuccess onJoinGroupSuccess) {
        this.onJoinGroupSuccess = onJoinGroupSuccess;
    }

    public void setOnDebtUpdated(OnDeptUpdated onDebtUpdated) {
        this.onDebtUpdated = onDebtUpdated;
    }

    public void setOnCurrentGroupNull(OnCurrentGroupNull onCurrentGroupNull) {
        this.onCurrentGroupNull = onCurrentGroupNull;
    }

    public void removeOnExpensesUpdated() {
        this.onExpensesUpdated = null;
    }

    public void removeOnJoinGroupError() {
        this.onJoinGroupError = null;
    }

    public void removeOnExpensePushed() {
        this.onExpensePushed = null;
    }

    public void removeOnJoinGroupSuccess() {
        this.onJoinGroupSuccess = null;
    }

    public void removeOnDebtUpdated() {
        this.onDebtUpdated = null;
    }

    public void removeOnCurrentDataNull() {
        this.onCurrentGroupNull = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Group getCurrentGroup() {
        return currentGroup;
    }

    public ArrayList<Expense> getDebtExpenses() {
        return debtManager.getExpenses();
    }

    public void setOnExpensesUpdated(OnExpensesUpdated onExpensesUpdated) {
        this.onExpensesUpdated = onExpensesUpdated;
    }

    private Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", currentUser.getName());
        result.put("currentGroup", currentShallowGroup.toMap());
        Map<String, Object> nested = new HashMap<>();
        groups.forEach(group -> {
            nested.putAll(group.toMap());
        });
        result.put("groups", nested);
        return result;
    }

    public interface OnExpensePushed {
        public void onExpensePushed();
    }

    public interface OnGroupPushed {
        public void onGroupPushed();
    }


    public interface OnGroupUpdated {
        public void onGroupUpdated(Group group);
    }

    public interface OnExpensesUpdated {
        public void onExpensesUpdated(ArrayList<Expense> expenses);
    }

    public interface OnJoinGroupError {
        public void onJoinGroupError();
    }

    public interface OnJoinGroupSuccess {
        public void onJoinGroupSuccess();
    }

    public interface OnDeptUpdated {
        public void onDebtUpdated(ArrayList<Expense> expenses);
    }

    public interface OnCurrentGroupNull {
        public void onCurrentGroupNull();
    }
}
