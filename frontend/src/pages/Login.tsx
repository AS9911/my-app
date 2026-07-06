import { useState } from "react";
import { Link as RouterLink } from "react-router-dom";
import {
    Box,
    Button,
    Card,
    CardContent,
    Container,
    Link,
    TextField,
    Typography
} from "@mui/material";
import { login } from "../services/authServices"


export default function LoginPage() {

    const [userId, setUserId] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    
    // ログイン押下時、APIへ通信
    const loginHandler = async (): Promise<void> => {
        try {
            const response = await login({
                userId,
                password
            })
            
            console.log(response);
            alert("ログイン成功");

        } catch (error) {
            console.log(error);
            alert(error);
        }
    }

    return (
        <Container maxWidth="sm">
            <Box
                sx={{
                    minHeight: "100vh",
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    bgcolor: "#f5f9ff"
                }}
            >
                <Card
                    elevation={5}
                    sx={{
                        width: "100%",
                        maxwidth: 420,
                        borderRadius: 3
                    }}
                >
                    <CardContent sx={{ p:5 }}>
                        <Typography
                            variant="h4"
                            align="center"
                            color="primary"
                            sx={{
                                fontWeight: "bold",
                                mb: 3
                            }}
                        >
                            ログイン
                        </Typography>

                        <TextField
                            label="ユーザーID"
                            fullWidth
                            margin="normal"
                            value={userId}
                            onChange={(e) => {
                                setUserId(e.target.value)
                            }}
                        />

                        <TextField
                            label="パスワード"
                            type="password"
                            fullWidth
                            margin="normal"
                            value={password}
                            onChange={(e) => {
                                setPassword(e.target.value)
                            }}
                        />

                        <Box
                            sx={{
                                display: "flex",
                                justifyContent: "center",
                                mt: 4
                            }}
                        >

                            <Button
                                variant="contained"
                                size="large"
                                sx={{
                                    width: 180,
                                    borderRadius: 2
                                }}
                                onClick={loginHandler}
                            >
                                ログイン
                            </Button>
                        </Box>

                        <Box
                            sx={{
                                mt: 3,
                                textAlign: "center"
                            }}
                        >
                            <Link
                                component={RouterLink}
                                to="/register"
                                underline="hover"
                            >
                                新規登録はこちら
                            </Link>
                        </Box>
                    </CardContent>
                </Card>
            </Box>
        </Container>
    )

}