package com.wisedaimyo.chesstraining.main.data.di

import android.app.Application
import android.content.Context
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.wisedaimyo.chesstraining.Constants.SIGN_IN_REQUEST
import com.wisedaimyo.chesstraining.Constants.SIGN_UP_REQUEST
import com.wisedaimyo.chesstraining.Constants.USERS
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.main.data.AuthRepositoryImpl
import com.wisedaimyo.chesstraining.main.data.AuthGoogleRepositoryImpl
import com.wisedaimyo.chesstraining.main.data.ChessCourseRepositoryImpl
import com.wisedaimyo.chesstraining.main.data.InvitationRepositoryImpl
import com.wisedaimyo.chesstraining.main.data.PlayingGameRepositoryImpl
import com.wisedaimyo.chesstraining.main.data.ProfileRepositoryImpl
import com.wisedaimyo.chesstraining.main.data.TrainerChessTaskRepositoryImpl
import com.wisedaimyo.chesstraining.main.data.TrainerCourseRepositoryImpl
import com.wisedaimyo.chesstraining.main.data.TrainerDateTaskRepositoryImpl
import com.wisedaimyo.chesstraining.main.data.UserRepositoryImpl
import com.wisedaimyo.chesstraining.main.data.PlayedGameRepositoryImpl
import com.wisedaimyo.chesstraining.main.data.models.use_case.users.AddUser
import com.wisedaimyo.chesstraining.main.data.models.use_case.users.ChangeImage
import com.wisedaimyo.chesstraining.main.data.models.use_case.users.ChangeName
import com.wisedaimyo.chesstraining.main.data.models.use_case.users.DeleteUser
import com.wisedaimyo.chesstraining.main.data.models.use_case.users.GetCurrentUser
import com.wisedaimyo.chesstraining.main.data.models.use_case.users.GetUserWithName
import com.wisedaimyo.chesstraining.main.data.models.use_case.users.GetUsers
import com.wisedaimyo.chesstraining.main.data.models.use_case.users.GetUsersWithIds
import com.wisedaimyo.chesstraining.main.data.models.use_case.users.ResetElo
import com.wisedaimyo.chesstraining.main.data.models.use_case.users.UseCaseUsers
import com.wisedaimyo.chesstraining.main.data.repository.AuthGoogleRepository
import com.wisedaimyo.chesstraining.main.data.repository.AuthRepository
import com.wisedaimyo.chesstraining.main.data.repository.ChessCourseRepository
import com.wisedaimyo.chesstraining.main.data.repository.InvitationRepository
import com.wisedaimyo.chesstraining.main.data.repository.PlayedGameRepository
import com.wisedaimyo.chesstraining.main.data.repository.PlayingGameRepository
import com.wisedaimyo.chesstraining.main.data.repository.ProfileRepository
import com.wisedaimyo.chesstraining.main.data.repository.TrainerChessTaskRepository
import com.wisedaimyo.chesstraining.main.data.repository.TrainerCourseRepository
import com.wisedaimyo.chesstraining.main.data.repository.TrainerDateTaskRepository
import com.wisedaimyo.chesstraining.main.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
class AppModule {
    @Provides
    fun provideChessCourseRepository(
        auth: FirebaseAuth,
        db: FirebaseFirestore
    ): ChessCourseRepository = ChessCourseRepositoryImpl(auth, db)

    @Provides
    fun provideTrainerChessDateTaskRepository(
        auth: FirebaseAuth,
        db: FirebaseFirestore
    ): TrainerDateTaskRepository = TrainerDateTaskRepositoryImpl(auth, db)

    @Provides
    fun provideTrainerChessTaskRepository(
        auth: FirebaseAuth,
        db: FirebaseFirestore
    ): TrainerChessTaskRepository = TrainerChessTaskRepositoryImpl(auth, db)

    @Provides
    fun provideTrainerCourseRepository(
        auth: FirebaseAuth,
        db: FirebaseFirestore
    ): TrainerCourseRepository = TrainerCourseRepositoryImpl(auth, db)

    @Provides
    fun providePlayedGameRepository(
        auth: FirebaseAuth,
        db: FirebaseFirestore
    ): PlayedGameRepository = PlayedGameRepositoryImpl(auth, db)

    @Provides
    fun provideAuthRepository(): AuthRepository = AuthRepositoryImpl(
        auth = Firebase.auth
    )

    @Provides
    fun provideInvitationRepository(
        auth: FirebaseAuth,
        db: FirebaseFirestore
    ): InvitationRepository = InvitationRepositoryImpl(auth, db)

    @Provides
    fun provideUsersRef() = Firebase.firestore.collection(USERS)

    @Provides
    fun provideUsersRepository(
        usersRef: CollectionReference
    ): UserRepository = UserRepositoryImpl(usersRef)

    @Provides
    fun provideUsersUseCases(
        repo: UserRepository
    ) = UseCaseUsers(
        getUsers = GetUsers(repo),
        getCurrentUser = GetCurrentUser(repo),
        addUser = AddUser(repo),
        deleteUser = DeleteUser(repo),
        getUserWithName = GetUserWithName(repo),
        getUsersWithIds = GetUsersWithIds(repo),
        changeImage = ChangeImage(repo),
        updateName = ChangeName(repo),
        resetElo = ResetElo(repo)
    )

    @Provides
    fun providePlayingRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): PlayingGameRepository = PlayingGameRepositoryImpl(auth, firestore)

    @Provides
    fun provideFirebaseAuth() = Firebase.auth

    @Provides
    fun provideFirebaseFirestore() = Firebase.firestore

    @Provides
    fun provideOneTapClient(
        @ApplicationContext
        context: Context
    ) = Identity.getSignInClient(context)

    @Provides
    @Named(SIGN_IN_REQUEST)
    fun provideSignInRequest(
        app: Application
    ) = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(app.getString(R.string.web_client_id))
                .setFilterByAuthorizedAccounts(true)
                .build())
        .setAutoSelectEnabled(true)
        .build()

    @Provides
    @Named(SIGN_UP_REQUEST)
    fun provideSignUpRequest(
        app: Application
    ) = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(app.getString(R.string.web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .build())
        .build()

    @Provides
    fun provideGoogleSignInOptions(
        app: Application
    ) = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(app.getString(R.string.web_client_id))
        .requestEmail()
        .build()

    @Provides
    fun provideGoogleSignInClient(
        app: Application,
        options: GoogleSignInOptions
    ) = GoogleSignIn.getClient(app, options)

    @Provides
    fun provideAuthGoogleRepository(
        auth: FirebaseAuth,
        oneTapClient: SignInClient,
        @Named(SIGN_IN_REQUEST)
        signInRequest: BeginSignInRequest,
        @Named(SIGN_UP_REQUEST)
        signUpRequest: BeginSignInRequest,
        db: FirebaseFirestore
    ): AuthGoogleRepository = AuthGoogleRepositoryImpl(
        auth = auth,
        oneTapClient = oneTapClient,
        signInRequest = signInRequest,
        signUpRequest = signUpRequest,
        db = db
    )

    @Provides
    fun provideProfileRepository(
        auth: FirebaseAuth,
        oneTapClient: SignInClient,
        signInClient: GoogleSignInClient,
        db: FirebaseFirestore
    ): ProfileRepository = ProfileRepositoryImpl(
        auth = auth,
        oneTapClient = oneTapClient,
        signInClient = signInClient,
        db = db
    )
}