package com.amany.taks

import android.os.Bundle
import androidx.activity.*
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.amany.taks.alarm.AlarmAppScreen
import com.amany.taks.fav.FavoriteScreen
import com.amany.taks.home.HomeScreen
import com.amany.taks.models.local.db.WeatherLocalDataSourceImpl
import com.amany.taks.models.remote.RemoteDataSource
import com.amany.taks.models.remote.RetrofitHelper.retrofit
import com.amany.taks.models.remote.WeathreService
import com.amany.taks.nav.Constants
import com.amany.taks.repository.WeatherRepository
import com.amany.taks.settings.SettingsScreen
import com.amany.taks.ui.theme.TAKSTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val localDataSource = WeatherLocalDataSourceImpl.getInstance(this)
        val remoteDataSource = RemoteDataSource.getInstance(retrofit.create(WeathreService::class.java))
        val weatherRepository = WeatherRepository.getInstance(remoteDataSource, localDataSource)

        enableEdgeToEdge()
        setContent {
            TAKSTheme (dynamicColor = false, darkTheme = false) {
                val navController = rememberNavController()
                Surface(color = Color.White) {
                    // Scaffold Component
                    Scaffold(
                        // Bottom navigation
                        bottomBar = {
                            BottomNavigationBar(navController = navController)
                        }, content = { padding ->
                            // Nav host: where screens are placed
                            NavHostContainer(navController = navController, padding = padding,weatherRepository)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues,
    weatherRepository: WeatherRepository
) {

    NavHost(
        navController = navController,



        startDestination = "home",

        modifier = Modifier.padding(paddingValues = padding),

        builder = {


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
        })
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
