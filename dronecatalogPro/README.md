# FPV Parts Pro (dronecatalogPro)

FPV Parts Pro is V2 of the FPV drone parts catalog. It keeps the V1 catalog (16 parts in 7 categories) and adds accounts, saved builds, flying spots on a map, and a flying-weather check per spot. It is a separate app: package `com.mitmarcus.dronecatalogpro`, so V1 (FPV Parts) and Pro install side by side.

## List of technologies

| Technology | Library | Used for |
|---|---|---|
| Firebase Authentication | `firebase-auth` (BoM 34.19.0) | Email and password sign up, sign in, sign out |
| Cloud Firestore | `firebase-firestore` | User profiles, drone builds with part quantities, flying spots |
| HTTP client for an external API | Retrofit 3.0.0 + kotlinx.serialization converter | Calls the Open-Meteo forecast API (`api.open-meteo.com/v1/forecast`), no SDK and no API key |
| GPS and maps | `play-services-location` 21.4.0, osmdroid 6.1.20 | Saves the phone's GPS position as a flying spot and shows all spots as markers on an OpenStreetMap map |

Supporting libraries: Jetpack Compose (Material 3), Navigation Compose 2.10.2 with type-safe routes, Lifecycle ViewModel 2.11.0, kotlinx-coroutines-play-services 1.10.2 (turns Firebase and location `Task`s into `suspend` calls).

## How each requirement is met

- **Kotlin:** all source is Kotlin.
- **Coroutines:** every Firebase, location and HTTP call is a `suspend` function run from `viewModelScope`. Firestore snapshot listeners are wrapped in `callbackFlow` (`FirestoreRepository.kt`) and collected with `collectAsStateWithLifecycle`.
- **Jetpack Compose:** all UI. The osmdroid `MapView` is embedded with `AndroidView` (`OsmMap.kt`).
- **Compose Navigation:** one activity, one `NavHost` with 9 destinations (`navigation/AppNavHost.kt`).
- **At least four screens besides sign in and out:** 7 screens: Home (categories), Part list, Part detail, My builds, Build detail, Flying spots map, Spot weather.
- **Firebase Authentication:** `AuthRepository.kt`. The app starts on Home when Firebase already has a signed-in user.
- **Firestore with more than users:** `users/{uid}` holds the profile; `users/{uid}/builds` and `users/{uid}/spots` hold the user's data. Adding a part uses `FieldValue.increment` on `parts.<partId>`, so a build can hold 4 of the same motor.
- **External HTTP API through an HTTP client:** `data/weather/WeatherApi.kt` (Retrofit interface).
- **Additional technology (GPS and maps):** `LocationHelper.kt` asks for the location permission and gets a fresh fix from the fused location provider; `SpotsMapScreen.kt` saves it and draws markers.

The weather screen does more than print the response: `FlyingConditions.kt` turns wind, gusts, rain and the WMO weather code into a Good / Fly with care / Stay on the ground verdict and picks the calmest dry hour in the next 12. The limits (wind 7 m/s, gusts 10 and 14 m/s, rain chance 30%) are the app's own rule of thumb. Unit tests for this are in `app/src/test`.

## Firebase setup (needed before first run)

The repo has no `google-services.json`, so the build fails until you add one.

1. In the [Firebase console](https://console.firebase.google.com), create a project (or reuse one) and add an Android app with package name `com.mitmarcus.dronecatalogpro`.
2. Download `google-services.json` into `app/`.
3. Authentication > Sign-in method: enable **Email/Password**.
4. Firestore Database: create a database, then paste `firestore.rules` into the Rules tab and publish.

The map and weather need no keys. The OpenStreetMap tile servers only require a user agent, which the app sets to its package name.

## Firestore layout

```
users/{uid}                  { name, email, createdAt }
users/{uid}/builds/{buildId} { name, parts: { "<partId>": quantity }, createdAt }
users/{uid}/spots/{spotId}   { name, latitude, longitude, createdAt }
```
