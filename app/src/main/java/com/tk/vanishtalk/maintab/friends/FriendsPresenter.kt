package com.tk.vanishtalk.maintab.friends

import com.google.firebase.auth.FirebaseAuth
import com.tk.vanishtalk.model.data.local.LocalChatRoom
import com.tk.vanishtalk.model.data.local.LocalUser
import com.tk.vanishtalk.model.SQLiteAdapter
import com.tk.vanishtalk.model.SharedPreferencesAdapter
import com.tk.vanishtalk.model.data.remote.RemoteUser
import com.tk.vanishtalk.model.repository.UserRepository
import com.tk.vanishtalk.util.Utils
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Response

class FriendsPresenter: FriendsContract.Presenter {

    var view: FriendsContract.View? = null
    var adapterModel: FriendsAdapterContract.Model? = null
    var adapterView: FriendsAdapterContract.View? = null
    set(value) {
        field = value
        field?.onFriendsClickListenerCallback = { onFriendClick(it) }
    }

    val compositeDisposable = CompositeDisposable()

    lateinit var localSQLModel: SQLiteAdapter
    lateinit var localSPModel: SharedPreferencesAdapter

    override fun loadMyInfo() {
        val myInfo = localSPModel.getCurrentUserData()
        view?.setMyInfo(myInfo)
    }

    override fun loadAllFriends() {
        view?.showProgress()
        FirebaseAuth.getInstance().currentUser?.let { user ->
            compositeDisposable.add(UserRepository
                .getFriendsList(user.uid, object: UserRepository.RetrofitGetListCallback {
                    override fun onSuccess(userResult: Response<ArrayList<RemoteUser>>) {
                        userResult.body()?.let {
                            localSQLModel.syncAllFriends(user.email!!, it)
                            view?.hideProgress()
                        }
                    }

                    override fun onError() {

                    }
                })
            )
        }

        localSPModel.getEmail()?.let {
            val friendsList = localSQLModel.selectAllFriends(it)
            adapterModel?.initAllFriends(friendsList)
            adapterView?.notifyDataChange()
        }
    }

    override fun addNewFriend() {
        view?.startAddFriendActivity()
    }

    override fun sendNewFriendToAdapter(friend: LocalUser) {
        adapterModel?.addFriends(friend)
        adapterView?.notifyDataChange()
    }

    override fun onFriendClick(friend: LocalUser) {
        view?.showFriend(friend)
    }

    override fun callToFriend(friend: LocalUser) {
        friend.phoneNum?.let {
            view?.startCallFriendIntent(it)
        }
    }

    override fun chatToFriend(friend: LocalUser) {
        localSPModel.getEmail()?.let {
            val roomMemberEmailList = ArrayList<String>()
            roomMemberEmailList.add(it)
            roomMemberEmailList.add(friend.email)
            val roomMemberEmailListJson = Utils.arrayListToJson(roomMemberEmailList)

            val isChatRoomMade = localSQLModel.isChatRoomExistsByUserEmailJson(roomMemberEmailListJson)
            when(isChatRoomMade) {
                true -> {
                    val chatRoom = localSQLModel.selectChatRoomByRoomMemberEmailJson(roomMemberEmailListJson)
                    view?.openExistChatRoom(chatRoom)
                }

                false -> {
                    val chatRoom = LocalChatRoom(
                        null,
                        Utils.makeDefaultRoomName(roomMemberEmailList),
                        roomMemberEmailListJson,
                        null,
                        null,
                        null
                    )
                    view?.openNewChatRoom(chatRoom)
                }
            }
        }
    }

    override fun close() {
        view = null
        compositeDisposable.clear()
    }
}