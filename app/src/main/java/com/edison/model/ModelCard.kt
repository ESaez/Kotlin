package com.edison.model

import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.PrimaryKey

open class ModelCard : RealmObject() {

    @PrimaryKey
    var title: String? = null

    var author: String? = null
    var createdAt: String? = null
    var story_url: String? = null

    fun additem(item: ModelCard) {
        var realm = Realm.getDefaultInstance()
        try {
            realm.beginTransaction()
            realm.copyToRealmOrUpdate(item)
            realm.commitTransaction()
        } finally {
            realm.close()
        }
    }

    fun getAll(): List<ModelCard> {
        var realm = Realm.getDefaultInstance()
        var lista: List<ModelCard> = realm.where(ModelCard::class.java).findAll().toList()
        return lista

    }

    fun deleteAll() {
        var realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            var realmResults = realm.where(ModelCard::class.java).findAll()
            realmResults.deleteAllFromRealm()
        }


    }

    fun deleteWhere(title : String){
        var realm = Realm.getDefaultInstance()
        realm.executeTransaction {

            var realmResults = realm.where(ModelCard::class.java).equalTo("title",title).findAll()
            realmResults.deleteAllFromRealm()
        }

    }

}