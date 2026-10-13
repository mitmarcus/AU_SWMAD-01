package com.mitmarcus.dronecatalogpro.navigation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mitmarcus.dronecatalogpro.data.AuthRepository
import com.mitmarcus.dronecatalogpro.data.PartCategory
import com.mitmarcus.dronecatalogpro.data.PartRepository
import com.mitmarcus.dronecatalogpro.ui.auth.SignInScreen
import com.mitmarcus.dronecatalogpro.ui.auth.SignUpScreen
import com.mitmarcus.dronecatalogpro.ui.builds.BuildDetailScreen
import com.mitmarcus.dronecatalogpro.ui.builds.BuildsScreen
import com.mitmarcus.dronecatalogpro.ui.catalog.HomeScreen
import com.mitmarcus.dronecatalogpro.ui.catalog.PartDetailScreen
import com.mitmarcus.dronecatalogpro.ui.catalog.PartListScreen
import com.mitmarcus.dronecatalogpro.ui.spots.SpotDetailScreen
import com.mitmarcus.dronecatalogpro.ui.spots.SpotsMapScreen

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    // Skip the sign-in screen when Firebase still has a signed-in user
    val startDestination: Any = remember { if (AuthRepository.currentUser != null) Home else SignIn }

    // New screens slide in from the right, and the old one moves a bit to the left behind it.
    // Going back does the reverse. The back swipe gesture plays the same animation.
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideIntoContainer(SlideDirection.Start, tween(300)) },
        exitTransition = { slideOutOfContainer(SlideDirection.Start, tween(300), targetOffset = { it / 4 }) },
        popEnterTransition = { slideIntoContainer(SlideDirection.End, tween(300), initialOffset = { it / 4 }) },
        popExitTransition = { slideOutOfContainer(SlideDirection.End, tween(300)) }
    ) {

        composable<SignIn> {
            SignInScreen(
                onSignedIn = { navController.goHomeClearingAuth() },
                onCreateAccount = { navController.navigate(SignUp) }
            )
        }

        composable<SignUp> {
            SignUpScreen(
                onSignedUp = { navController.goHomeClearingAuth() },
                onBack = { navController.popBackStack() }
            )
        }

        composable<Home> {
            HomeScreen(
                userName = AuthRepository.currentUser?.displayName
                    ?: AuthRepository.currentUser?.email.orEmpty(),
                categories = PartRepository.getCategories(),
                onCategoryClick = { navController.navigate(PartList(it.name)) },
                onBuildsClick = { navController.navigate(Builds) },
                onSpotsClick = { navController.navigate(Spots) },
                onSignOut = {
                    AuthRepository.signOut()
                    navController.navigate(SignIn) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable<PartList> { entry ->
            val category = PartCategory.valueOf(entry.toRoute<PartList>().category)
            PartListScreen(
                title = category.title,
                parts = PartRepository.getPartsByCategory(category),
                onBack = { navController.popBackStack() },
                onPartClick = { navController.navigate(PartDetail(it.id)) }
            )
        }

        composable<PartDetail> { entry ->
            PartDetailScreen(
                part = PartRepository.getPartById(entry.toRoute<PartDetail>().partId),
                onBack = { navController.popBackStack() },
                onOpenBuild = { navController.navigate(BuildDetail(it)) }
            )
        }

        composable<Builds> {
            BuildsScreen(
                onBack = { navController.popBackStack() },
                onBuildClick = { navController.navigate(BuildDetail(it.id)) }
            )
        }

        composable<BuildDetail> {
            BuildDetailScreen(
                onBack = { navController.popBackStack() },
                onPartClick = { navController.navigate(PartDetail(it)) }
            )
        }

        composable<Spots> {
            SpotsMapScreen(
                onBack = { navController.popBackStack() },
                onSpotClick = { navController.navigate(SpotDetail(it.id)) }
            )
        }

        composable<SpotDetail> {
            SpotDetailScreen(onBack = { navController.popBackStack() })
        }
    }
}

// After sign in or sign up, remove the auth screens so back does not return to them
private fun NavHostController.goHomeClearingAuth() {
    navigate(Home) {
        popUpTo(graph.id) { inclusive = true }
    }
}
