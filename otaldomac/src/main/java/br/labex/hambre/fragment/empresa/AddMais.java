package br.labex.hambre.fragment.empresa;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import br.labex.hambre.helper.FirebaseHelper;

public class AddMais {
    public static void salvar(List<String> addMaisList){
        DatabaseReference addMaisRef = FirebaseHelper.getDatabaseReference()
                .child("addMais")
                .child(FirebaseHelper.getIdFirebase());
        addMaisRef.setValue(addMaisList);
    }
}
