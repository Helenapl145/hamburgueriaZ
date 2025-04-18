package com.example.hamburgueriaz

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hamburgueriaz.ui.theme.HamburgueriaZTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HamburgueriaZTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OrderScreen()
                }
            }
        }
    }
}

@Composable
fun OrderScreen() {
    val context = LocalContext.current
    var customerName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf(0) }

    val extras = listOf("Bacon", "Queijo", "Onion Rings")
    val selectedExtras = remember { mutableStateMapOf<String, Boolean>() }

    val prices = mapOf(
        "Bacon" to 2.0,
        "Queijo" to 2.0,
        "Onion Rings" to 3.0
    )

    val basePrice = 20.0
    val totalExtras = selectedExtras.filter { it.value }
        .map { prices[it.key] ?: 0.0 }
        .sum()

    val totalPrice = (basePrice + totalExtras) * quantity

    Column(modifier = Modifier.padding(16.dp)) {
        Image(
            painter = painterResource(id = R.drawable.banner_image),  // Substitua 'banner_image' pelo nome real da sua imagem
            contentDescription = "Banner da Hamburgueria",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)  // Ajuste a altura da imagem
                .clip(shape = MaterialTheme.shapes.medium)
                .border(2.dp, MaterialTheme.colorScheme.primary)
        )

        Spacer(modifier = Modifier.height(16.dp))
        // Nome do cliente
        OutlinedTextField(
            value = customerName,
            onValueChange = { customerName = it },
            label = { Text("Nome do Cliente") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Faça seu pedido", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        // Adicionais
        Text("Escolha os adicionais:")
        extras.forEach { extra ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .toggleable(
                        value = selectedExtras[extra] ?: false,
                        onValueChange = { isChecked ->
                            selectedExtras[extra] = isChecked
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selectedExtras[extra] ?: false,
                    onCheckedChange = null
                )
                Text(text = extra, modifier = Modifier.padding(start = 8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quantidade
        Text("Quantidade", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { if (quantity > 0) quantity-- }) {
                Text("-")
            }
            Text(
                text = quantity.toString(),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Button(onClick = { quantity++ }) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preço total
        Text("Resumo do pedido", style = MaterialTheme.typography.titleMedium)
        Text("Preço total: R$ %.2f".format(totalPrice))

        Spacer(modifier = Modifier.height(24.dp))

        // Botão Enviar Pedido
        Button(
            onClick = {
                val resumo = buildString {
                    appendLine("Nome do cliente: $customerName")
                    appendLine("Tem Bacon? ${if (selectedExtras["Bacon"] == true) "Sim" else "Não"}")
                    appendLine("Tem Queijo? ${if (selectedExtras["Queijo"] == true) "Sim" else "Não"}")
                    appendLine("Tem Onion Rings? ${if (selectedExtras["Onion Rings"] == true) "Sim" else "Não"}")
                    appendLine("Quantidade: $quantity")
                    appendLine("Preço final: R$ %.2f".format(totalPrice))
                }

                // Mostra Toast com resumo
                Toast.makeText(context, resumo, Toast.LENGTH_LONG).show()

                // Envia e-mail
                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "message/rfc822"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("contato@hamburgueriaZ.com")) // ou insira um destinatário fixo se quiser
                    putExtra(Intent.EXTRA_SUBJECT, "Pedido de $customerName")
                    putExtra(Intent.EXTRA_TEXT, resumo)
                }

                try {
                    context.startActivity(
                        Intent.createChooser(emailIntent, "Escolha um app de e-mail")
                    )
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Nenhum app de e-mail encontrado!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar Pedido")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderScreenPreview() {
    HamburgueriaZTheme {
        OrderScreen()
    }
}
