package com.example.explorelocal.data.model

import org.osmdroid.util.GeoPoint

data class UmkmLocation(
    val id: String,
    val name: String,
    val category: String,
    val description: String, // Field Baru
    val photoUrl: String,    // Field Baru
    val geoPoint: GeoPoint
)

// Data Dummy dengan Deskripsi dan Foto (Placeholder)
val dummyUmkmLocations = listOf(
    UmkmLocation(
        id = "1",
        name = "Bakso President",
        category = "Makanan",
        description = "Bakso legendaris khas Malang yang terletak di pinggir rel kereta api. Sensasi makan bakso dengan getaran kereta lewat.",
        photoUrl = "https://picsum.photos/seed/bakso/200/200", // Gambar dummy
        geoPoint = GeoPoint(-7.9644, 112.6369)
    ),
    UmkmLocation(
        id = "2",
        name = "Toko Oen",
        category = "Cafe & Resto",
        description = "Restoran tempo dulu yang menyajikan es krim dan masakan kolonial Belanda sejak tahun 1930-an.",
        photoUrl = "https://picsum.photos/seed/tokooen/200/200",
        geoPoint = GeoPoint(-7.9826, 112.6308)
    ),
    UmkmLocation(
        id = "3",
        name = "Rawon Nguling",
        category = "Makanan",
        description = "Rawon dengan kuah hitam pekat yang gurih dan potongan daging empuk. Wajib dicoba saat ke Malang.",
        photoUrl = "https://picsum.photos/seed/rawon/200/200",
        geoPoint = GeoPoint(-7.9786, 112.6356)
    ),
    UmkmLocation(
        id = "4",
        name = "Pos Ketan Legenda",
        category = "Jajanan",
        description = "Sajian ketan dengan berbagai macam topping, mulai dari durian, keju, hingga susu yang sangat populer di Batu.",
        photoUrl = "https://picsum.photos/seed/ketan/200/200",
        geoPoint = GeoPoint(-7.8841, 112.5248)
    ),
    UmkmLocation(
        id = "5",
        name = "Pecel Kawi",
        category = "Makanan",
        description = "Nasi pecel legendaris dengan bumbu kacang yang kental dan gurih, berdiri sejak tahun 1975.",
        photoUrl = "https://picsum.photos/seed/pecel/200/200",
        geoPoint = GeoPoint(-7.9763, 112.6234)
    )
)