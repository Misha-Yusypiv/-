package com.yourappname.petapp.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.fragment.fragment
import androidx.navigation.navigation
import com.yourappname.petapp.presentation.auth.LoginFragment
import com.yourappname.petapp.presentation.auth.CodeFragment
import com.yourappname.petapp.presentation.loading.LoadingFragment
import com.yourappname.petapp.presentation.feed.FeedFragment
import com.yourappname.petapp.presentation.help.HelpFragment
import com.yourappname.petapp.presentation.documents.DocumentsFragment
import com.yourappname.petapp.presentation.documents.DocumentDetailFragment
import com.yourappname.petapp.presentation.mypets.MyPetsFragment
import com.yourappname.petapp.presentation.services.ServicesFragment
import com.yourappname.petapp.presentation.menu.MenuFragment

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Code : Screen("code")
    object Loading : Screen("loading")
    object Feed : Screen("feed")
    object Help : Screen("help")
    object Documents : Screen("documents")
    object DocumentDetail : Screen("document_detail/{documentId}") {
        fun createRoute(documentId: String) = "document_detail/$documentId"
    }
    object MyPets : Screen("my_pets")
    object Services : Screen("services")
    object Menu : Screen("menu")
}

fun NavGraphBuilder.appNavGraph(navController: NavController): NavGraph {
    return navigation(
        startDestination = Screen.Login.route,
        route = "app_graph"
    ) {
        fragment(Screen.Login.route) {
            LoginFragment()
        }
        fragment(Screen.Code.route) {
            CodeFragment()
        }
        fragment(Screen.Loading.route) {
            LoadingFragment()
        }
        fragment(Screen.Feed.route) {
            FeedFragment()
        }
        fragment(Screen.Help.route) {
            HelpFragment()
        }
        fragment(Screen.Documents.route) {
            DocumentsFragment()
        }
        fragment(Screen.DocumentDetail.route) {
            DocumentDetailFragment()
        }
        fragment(Screen.MyPets.route) {
            MyPetsFragment()
        }
        fragment(Screen.Services.route) {
            ServicesFragment()
        }
        fragment(Screen.Menu.route) {
            MenuFragment()
        }
    }
} 