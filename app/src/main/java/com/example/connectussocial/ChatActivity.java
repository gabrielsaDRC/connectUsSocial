package com.example.connectussocial;

import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;
import com.google.firebase.firestore.EventListener;
import com.squareup.picasso.*;
import com.xwray.groupie.*;
import com.xwray.groupie.ViewHolder;

import java.util.*;

public class ChatActivity extends AppCompatActivity {

    private GroupAdapter adapter;
    private User user;
    private EditText editChat;
    private User me;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_chat);

        user = getIntent().getExtras().getParcelable("user");
        getSupportActionBar().setTitle(user.getUsername());

        RecyclerView rv = findViewById(R.id.recycler_chat);
        Button btnChat = findViewById(R.id.btn_chat);
        editChat = findViewById(R.id.edit_chat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

       adapter = new GroupAdapter();
       rv.setLayoutManager(new LinearLayoutManager(this));
       rv.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("/Users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        me = documentSnapshot.toObject(User.class);
                        fetchMessages();
                    }
                });

        }

        //método que exibe mensagem nos baloes
        private void fetchMessages() {
            if (me != null) {

                String fromId = me.getUuid();
                String toId = user.getUuid();

                FirebaseFirestore.getInstance().collection("/conversations")
                        .document(fromId)
                        .collection(toId)
                        .orderBy("timestamp", Query.Direction.ASCENDING)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override

                            // metodo que verifica se a mensagem é adicionada
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                                if (documentChanges != null){
                                    for (DocumentChange doc: documentChanges) {
                                        if (doc.getType() == DocumentChange.Type.ADDED){
                                            Message message = doc.getDocument().toObject(Message.class);
                                            adapter.add(new MessageItem(message));
                                        }
                                    }
                                }

                            }
                        });
            }
    }

    private void sendMessage() {
        String text = editChat.getText().toString();
        editChat.setText(null);

        final String fromId = FirebaseAuth.getInstance().getUid();
        final String toId = user.getUuid();
        Long timestamp = System.currentTimeMillis();

        final Message message = new Message();
        message.setFromId(fromId);
        message.setToId(toId);
        message.setTimestamp(timestamp);
        message.setText(text);

        // sistema de cadastro de mensagem no firebase >> precisa passar pra sql

        if (!message.getText().isEmpty()) {
            FirebaseFirestore.getInstance().collection("/conversations")
                    .document(fromId)
                    .collection(toId)
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("teste", documentReference.getId());

                            Contact contact = new Contact();
                            contact.setUuid(toId);
                            contact.setUsername(user.getUsername());
                            contact.setPhotoUrl(user.getProfileUrl());
                            contact.setTimestamp(message.getTimestamp());
                            contact.setLastMessage(message.getText());


                            FirebaseFirestore.getInstance().collection("/last-messages")
                                    .document(fromId)
                                    .collection("contacts")
                                    .document(toId)
                                    .set(contact);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("teste", e.getMessage(), e);
                        }
                    });

            FirebaseFirestore.getInstance().collection("/conversations")
                    .document(toId)
                    .collection(fromId)
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("teste", documentReference.getId());


                            Contact contact = new Contact();
                            contact.setUuid(toId);
                            contact.setUsername(me.getUsername());
                            contact.setPhotoUrl(me.getProfileUrl());
                            contact.setTimestamp(message.getTimestamp());
                            contact.setLastMessage(message.getText());

                            FirebaseFirestore.getInstance().collection("/last-messages")
                                    .document(toId)
                                    .collection("contacts")
                                    .document(fromId)
                                    .set(contact);


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("teste", e.getMessage(), e);
                        }
                    });
        }
    }


    private class MessageItem extends Item<ViewHolder>{

        private final Message message;

        private MessageItem(Message message) {
            this.message = message;
        }


        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {

            TextView txtMsg = viewHolder.itemView.findViewById(R.id.txt_message);
            ImageView imgMessage = viewHolder.itemView.findViewById(R.id.img_message_user);

            txtMsg.setText(message.getText());
            Picasso.get()
                    .load(message.getFromId().equals(FirebaseAuth.getInstance().getUid())
                            ? me.getProfileUrl()
                            : user.getProfileUrl())
                    .into(imgMessage);
        }

        @Override
        public int getLayout() {

            // sistema que define quem ta mandando a mensagem no firebase

            return message.getFromId().equals(FirebaseAuth.getInstance().getUid())
                    ? R.layout.item_from_message
                    : R.layout.item_to_message;
        }
    }

}
