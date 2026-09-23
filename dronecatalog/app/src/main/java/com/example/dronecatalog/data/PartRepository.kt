package com.example.dronecatalog.data

// All the data for the app. Prices are approximate.
object PartRepository {

    private val parts = listOf(
        // Frames
        Part(
            1, "Source One V5", "TBS", PartCategory.FRAME,
            price = 35, keySpec = "5 inch - 226 mm",
            description = "Cheap open-source freestyle frame. Spare arms are easy to find.",
            specs = mapOf(
                "Size" to "5 inch",
                "Wheelbase" to "226 mm",
                "Material" to "Carbon fibre",
                "Stack mounting" to "30.5 × 30.5 mm and 20 × 20 mm"
            )
        ),
        Part(
            2, "Mark5", "GEPRC", PartCategory.FRAME,
            price = 60, keySpec = "5 inch - 225 mm",
            description = "Popular freestyle frame with a protected camera cage.",
            specs = mapOf(
                "Size" to "5 inch",
                "Wheelbase" to "225 mm",
                "Material" to "Carbon fibre",
                "Stack mounting" to "30.5 × 30.5 mm and 20 × 20 mm"
            )
        ),
        Part(
            3, "Apex 5", "ImpulseRC", PartCategory.FRAME,
            price = 100, keySpec = "5 inch - 223 mm",
            description = "Premium frame known for being very stiff and durable.",
            specs = mapOf(
                "Size" to "5 inch",
                "Wheelbase" to "223 mm",
                "Material" to "Carbon fibre",
                "Stack mounting" to "30.5 × 30.5 mm and 20 × 20 mm"
            )
        ),

        // Motors
        Part(
            4, "XING2 2207", "iFlight", PartCategory.MOTOR,
            price = 22, keySpec = "2207 - 1855KV",
            description = "Smooth all-round motor for 5 inch freestyle builds.",
            specs = mapOf(
                "Stator size" to "22 × 07 mm",
                "KV" to "1855KV",
                "Battery" to "6S",
                "Weight" to "about 32 g"
            )
        ),
        Part(
            5, "F60 Pro IV", "T-Motor", PartCategory.MOTOR,
            price = 28, keySpec = "2207 - 1950KV",
            description = "Powerful motor that is a common choice for racing.",
            specs = mapOf(
                "Stator size" to "22 × 07 mm",
                "KV" to "1950KV",
                "Battery" to "6S",
                "Weight" to "about 34 g"
            )
        ),
        Part(
            6, "ECO II 2306", "EMAX", PartCategory.MOTOR,
            price = 15, keySpec = "2306 - 1900KV",
            description = "Budget motor, a good choice for a first build.",
            specs = mapOf(
                "Stator size" to "23 × 06 mm",
                "KV" to "1900KV",
                "Battery" to "6S",
                "Weight" to "about 33 g"
            )
        ),

        // ESCs
        Part(
            7, "BLS 50A 4-in-1", "SpeedyBee", PartCategory.ESC,
            price = 45, keySpec = "50 A - 3-6S",
            description = "4-in-1 ESC that comes in the SpeedyBee F405 V4 stack.",
            specs = mapOf(
                "Current" to "50 A per motor",
                "Battery" to "3-6S",
                "Firmware" to "BLHeli_S",
                "Mounting" to "30.5 × 30.5 mm"
            )
        ),
        Part(
            8, "F55A Pro II 4-in-1", "T-Motor", PartCategory.ESC,
            price = 75, keySpec = "55 A - 3-6S",
            description = "Robust 4-in-1 ESC that handles hard flying well.",
            specs = mapOf(
                "Current" to "55 A per motor",
                "Battery" to "3-6S",
                "Firmware" to "BLHeli_32",
                "Mounting" to "30.5 × 30.5 mm"
            )
        ),

        // Flight controllers
        Part(
            9, "F405 V4", "SpeedyBee", PartCategory.FLIGHT_CONTROLLER,
            price = 40, keySpec = "F405 - 30.5 mm",
            description = "Beginner friendly flight controller that can be set up from a phone app.",
            specs = mapOf(
                "Processor" to "STM32F405",
                "Firmware" to "Betaflight, INAV",
                "Mounting" to "30.5 × 30.5 mm",
                "Bluetooth" to "Yes"
            )
        ),
        Part(
            10, "H743-SLIM V3", "Matek", PartCategory.FLIGHT_CONTROLLER,
            price = 65, keySpec = "H743 - 30.5 mm",
            description = "Fast flight controller often used for GPS and long range builds.",
            specs = mapOf(
                "Processor" to "STM32H743",
                "Gyro" to "Dual gyro",
                "Firmware" to "Betaflight, INAV, ArduPilot",
                "Mounting" to "30.5 × 30.5 mm",
                "Bluetooth" to "No"
            )
        ),

        // Cameras
        Part(
            11, "Ratel 2", "Caddx", PartCategory.CAMERA,
            price = 30, keySpec = "Analog - 1200TVL",
            description = "Analog camera with good low light performance.",
            specs = mapOf(
                "Type" to "Analog",
                "Resolution" to "1200TVL",
                "Size" to "19 × 19 mm"
            )
        ),
        Part(
            12, "Phoenix 2", "RunCam", PartCategory.CAMERA,
            price = 30, keySpec = "Analog - 1000TVL",
            description = "Analog camera that handles bright sky and dark ground well.",
            specs = mapOf(
                "Type" to "Analog",
                "Resolution" to "1000TVL",
                "Sensor" to "1/2 inch CMOS",
                "Size" to "19 × 19 mm"
            )
        ),

        // Video transmitters
        Part(
            13, "O3 Air Unit", "DJI", PartCategory.VTX,
            price = 229, keySpec = "Digital - 1080p",
            description = "Digital HD video system with the camera built in. Records 4K on the drone.",
            specs = mapOf(
                "Type" to "Digital",
                "Live video" to "1080p",
                "Recording" to "4K/60fps",
                "Frequency" to "5.8 GHz",
                "Camera" to "Included"
            )
        ),
        Part(
            14, "Unify Pro32 HV", "TBS", PartCategory.VTX,
            price = 50, keySpec = "Analog - 1000 mW",
            description = "Analog video transmitter with adjustable output power.",
            specs = mapOf(
                "Type" to "Analog",
                "Output power" to "25 / 200 / 500 / 1000 mW",
                "Frequency" to "5.8 GHz",
                "Battery" to "Up to 6S",
                "Camera" to "Not included"
            )
        ),

        // Antennas
        Part(
            15, "Lollipop 4", "Foxeer", PartCategory.ANTENNA,
            price = 12, keySpec = "5.8 GHz - 2.6 dBi",
            description = "Cheap and popular antenna, sold in pairs.",
            specs = mapOf(
                "Frequency" to "5.8 GHz",
                "Gain" to "2.6 dBi",
                "Polarization" to "RHCP",
                "Connector" to "UFL, SMA or MMCX"
            )
        ),
        Part(
            16, "AXII 2", "Lumenier", PartCategory.ANTENNA,
            price = 20, keySpec = "5.8 GHz - RHCP",
            description = "Durable antenna with a clear signal, often used on the goggles side.",
            specs = mapOf(
                "Frequency" to "5.8 GHz",
                "Polarization" to "RHCP",
                "Connector" to "UFL, SMA or MMCX"
            )
        )
    )

    fun getCategories(): List<PartCategory> = PartCategory.entries

    fun getPartsByCategory(category: PartCategory): List<Part> =
        parts.filter { it.category == category }

    fun getPartById(id: Int): Part? = parts.find { it.id == id }
}
