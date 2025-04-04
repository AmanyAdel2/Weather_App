package com.amany.taks

import android.os.Build
import android.os.Bundle
import androidx.activity.*
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.amany.taks.alarm.view.AlarmAppScreen
import com.amany.taks.fav.FavoriteScreen
import com.amany.taks.home.HomeScreen
import com.amany.taks.models.local.db.WeatherLocalDataSourceImpl
import com.amany.taks.models.remote.RemoteDataSource
import com.amany.taks.models.remote.RetrofitHelper.retrofit
import com.amany.taks.models.remote.WeathreService
import com.amany.taks.nav.Constants
import com.amany.taks.repository.WeatherRepository
import com.amany.taks.settings.SettingsScreen
import com.amany.taks.settings.setAppLocale
import com.amany.taks.ui.theme.TAKSTheme


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val localDataSource = WeatherLocalDataSourceImpl.getInstance(this)
        val remoteDataSource = RemoteDataSource.getInstance(retrofit.create(WeathreService::class.java))
        val weatherRepository = WeatherRepository.getInstance(remoteDataSource, localDataSource)
        setAppLocale(this)
        enableEdgeToEdge()
        setContent {
            TAKSTheme (dynamicColor = false, darkTheme = false) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                Surface(color = Color.White) {
                    Scaffold(
                        // Bottom navigation
                        bottomBar = {
                            if (currentRoute != "splash") { // Hide bottom bar on splash
                                BottomNavigationBar(navController = navController)
                            }}, content = { padding ->

                            NavHostContainer(navController = navController, padding = padding,weatherRepository)
                        }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues,
    weatherRepository: WeatherRepository
) {

    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = Modifier.padding(paddingValues = padding)
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("home") {
            HomeScreen()
        }
        composable("search") {
            AlarmAppScreen()
        }
        composable("settings") {
            SettingsScreen()
        }
        composable("favorite") {
            FavoriteScreen(weatherRepository)
        }
    }

}

@Composable
fun BottomNavigationBar(navController: NavHostController) {

    NavigationBar(


        containerColor = Color(0xFF0E1A64)
    ) {


        val navBackStackEntry by navController.currentBackStackEntryAsState()


        val currentRoute = navBackStackEntry?.destination?.route


        Constants.BottomNavItems.forEach { navItem ->


            NavigationBarItem(


                selected = currentRoute == navItem.route,


                onClick = {
                    navController.navigate(navItem.route)
                },


                icon = {
                    Icon(imageVector = navItem.icon, contentDescription = navItem.label)
                },


                label = {
                    Text(text = navItem.label)
                },
                alwaysShowLabel = false,

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = Color(0xFF439EC4)
                )
            )
        }
    }
}



@Composable
fun SplashScreen(navController: NavController) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("weather.json"))
    val progress by animateLottieCompositionAsState(composition)

    // Navigate to home screen when animation completes
    LaunchedEffect(progress) {
        if (progress == 1f) {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(260.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

        }
    }

}

